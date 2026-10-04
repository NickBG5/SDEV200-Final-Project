package com.sdev200.budgeting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:budgeting-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class BudgetApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void persistsUserBudgetAndExpenseAndReturnsUpdatedCategoryTotals() throws Exception {
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alex Example","email":"alex@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        long userId = readId(userResult);
        MvcResult budgetResult = mockMvc.perform(post("/api/users/{userId}/budgets", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"2026-10","monthlyIncome":4000.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categories[0].allocated").value(2000.0))
                .andExpect(jsonPath("$.categories[1].allocated").value(1200.0))
                .andExpect(jsonPath("$.categories[2].allocated").value(800.0))
                .andReturn();

        long budgetId = readId(budgetResult);
        mockMvc.perform(post("/api/users/{userId}/budgets/{budgetId}/expenses", userId, budgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Groceries","amount":125.50,"category":"NEEDS"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("NEEDS"));

        mockMvc.perform(get("/api/users/{userId}/budgets/{budgetId}", userId, budgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].spent").value(125.5))
                .andExpect(jsonPath("$.categories[0].remaining").value(1874.5));

        mockMvc.perform(get("/api/users/{userId}/budgets/{budgetId}/expenses", userId, budgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Groceries"));
    }

    @Test
    void doesNotExposeAnotherUsersBudgetThroughTheUserScopedRoute() throws Exception {
        MvcResult firstUser = createUser("first@example.com");
        MvcResult secondUser = createUser("second@example.com");
        long firstUserId = readId(firstUser);
        long secondUserId = readId(secondUser);

        MvcResult budgetResult = mockMvc.perform(post("/api/users/{userId}/budgets", firstUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"2026-11","monthlyIncome":1000.00}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(get("/api/users/{userId}/budgets/{budgetId}", secondUserId, readId(budgetResult)))
                .andExpect(status().isNotFound());
    }

    private MvcResult createUser(String email) throws Exception {
        return mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test User","email":"%s"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private long readId(MvcResult result) throws Exception {
        Number id = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
