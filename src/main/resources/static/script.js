const API_URL = "https://expensetracker-rq6o.onrender.com/api/expenses";

// ✅ Get logged-in email
const EMAIL = localStorage.getItem("userEmail");

// 🔐 If not logged in → redirect to login page
if (!EMAIL) {
    window.location.href = "login.html";
}

let editingId = null;
let chartInstance = null;
let allExpenses = [];

document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("expenseForm");
    const logDiv = document.getElementById("log");
    const table = document.querySelector("table");
    const totalDiv = document.getElementById("total");
    const categoryFilter = document.getElementById("categoryFilter");

    /* ================= LOAD EXPENSES ================= */
    async function loadExpenses() {
        try {

            const response = await fetch(`${API_URL}/all?email=${EMAIL}`);
            if (!response.ok) throw new Error("Failed to fetch");

            allExpenses = await response.json();

            populateCategoryDropdown();
            applyFilters();

        } catch (error) {
            if (logDiv) {
                logDiv.innerText = `❌ ${error.message}`;
            }
        }
    }

    /* ================= FILTER LOGIC ================= */
    window.applyFilters = function () {

        const selectedCategory = categoryFilter ? categoryFilter.value : "ALL";

        const startDateInput = document.getElementById("startDate");
        const endDateInput = document.getElementById("endDate");

        const startDate = startDateInput ? startDateInput.value : "";
        const endDate = endDateInput ? endDateInput.value : "";

        if (!table) return;

        table.querySelectorAll("tr:not(:first-child)").forEach(row => row.remove());

        let total = 0;
        let categorySummary = {};

        allExpenses.forEach(exp => {

            let matchCategory =
                selectedCategory === "ALL" || exp.category === selectedCategory;

            let matchDate = true;

            if (startDate) {
                matchDate = new Date(exp.date) >= new Date(startDate);
            }

            if (endDate) {
                matchDate = matchDate && new Date(exp.date) <= new Date(endDate);
            }

            if (matchCategory && matchDate) {

                total += exp.amount;

               let cleanCategory = exp.category.trim().toLowerCase();

categorySummary[cleanCategory] =
    (categorySummary[cleanCategory] || 0) + exp.amount;


                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${exp.title}</td>
                    <td>${exp.category}</td>
                    <td>${exp.amount}</td>
                    <td>${exp.date}</td>
                    <td>
                        <button onclick="editExpense(${exp.id}, '${exp.title}', '${exp.category}', ${exp.amount}, '${exp.date}')">Edit</button>
                        <button onclick="deleteExpense(${exp.id})">Delete</button>
                    </td>
                `;
                table.appendChild(row);
            }
        });

        if (totalDiv) {
            totalDiv.innerText = "Total: ₹ " + total;
        }

        renderChart(categorySummary);
    };

    /* ================= CATEGORY DROPDOWN ================= */
    function populateCategoryDropdown() {

        if (!categoryFilter) return;

        const categories = new Set(allExpenses.map(e => e.category));

        categoryFilter.innerHTML = `<option value="ALL">All Categories</option>`;

        categories.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat;
            option.textContent = cat;
            categoryFilter.appendChild(option);
        });
    }

    /* ================= DATE FILTER ================= */
    window.applyDateFilter = function () {
        applyFilters();
    };

    window.resetDateFilter = function () {

        const startDateInput = document.getElementById("startDate");
        const endDateInput = document.getElementById("endDate");

        if (startDateInput) startDateInput.value = "";
        if (endDateInput) endDateInput.value = "";

        applyFilters();
    };

    if (categoryFilter) {
        categoryFilter.addEventListener("change", applyFilters);
    }

    /* ================= ADD / UPDATE ================= */
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const expense = {
                title: document.getElementById("title").value,
                category: document.getElementById("category").value,
                amount: Number(document.getElementById("amount").value),
                date: document.getElementById("date").value
            };

            try {

                if (editingId === null) {

                    await fetch(`${API_URL}/save?email=${EMAIL}`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(expense)
                    });

                    alert("✅ Added!");

                } else {

                    await fetch(`${API_URL}/id/${editingId}?email=${EMAIL}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(expense)
                    });

                    alert("✅ Updated!");
                    editingId = null;
                }

                form.reset();
                await loadExpenses();

            } catch {
                alert("❌ Error saving expense");
            }
        });
    }

    loadExpenses();
});


/* ================= DELETE ================= */
async function deleteExpense(id) {
    await fetch(`/api/expenses/id/${id}?email=${EMAIL}`, { method: "DELETE" });
    location.reload();
}


/* ================= EDIT ================= */
function editExpense(id, title, category, amount, date) {

    document.getElementById("title").value = title;
    document.getElementById("category").value = category;
    document.getElementById("amount").value = amount;
    document.getElementById("date").value = date;

    editingId = id;
}


/* ================= LOGOUT ================= */
function logout() {
    localStorage.removeItem("userEmail");
    window.location.href = "login.html";
}


/* ================= BAR CHART ================= */
function renderChart(summary) {

    const canvas = document.getElementById("categoryChart");
    if (!canvas) return;

    const ctx = canvas.getContext("2d");

    if (chartInstance) {
        chartInstance.destroy();
    }

    chartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels: Object.keys(summary),
            datasets: [{
                label: "Total Amount by Category",
                data: Object.values(summary),
                backgroundColor: "rgba(54, 162, 235, 0.6)"
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

