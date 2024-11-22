function initialiseExpensesChart() {
    const ctx = document.getElementById('expensesChart').getContext('2d');

    // Fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            // Filter data to include only transactions of type 'EXPENSE'
            const expenseData = data.filter(transaction => transaction.type === 'EXPENSE');

            // Sort filtered data by date in ascending order (oldest first)
            expenseData.sort((a, b) => new Date(a.date) - new Date(b.date));

            // Process data into labels and values for the chart
            const labels = expenseData.map(transaction => new Date(transaction.date).toLocaleDateString());
            const values = expenseData.map(transaction => transaction.amount); // Replace with actual amount property

            // Create the chart
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Expenses',
                        data: values,
                        borderColor: 'rgba(255, 99, 132, 1)', // Red for expenses
                        backgroundColor: 'rgba(255, 99, 132, 0.2)', // Light red background
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                        },
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Date'
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Amount'
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
}

// Initialize the chart when the DOM content is loaded
document.addEventListener("DOMContentLoaded", initialiseExpensesChart);

function initialiseIncomeChart() {
    const ctx = document.getElementById('incomeChart').getContext('2d');

    // Fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            // Filter data to include only transactions of type 'EXPENSE'
            const incomeData = data.filter(transaction => transaction.type === 'INCOME');

            // Sort filtered data by date in ascending order (oldest first)
            incomeData.sort((a, b) => new Date(a.date) - new Date(b.date));

            // Process data into labels and values for the chart
            const labels = incomeData.map(transaction => new Date(transaction.date).toLocaleDateString());
            const values = incomeData.map(transaction => transaction.amount); // Replace with actual amount property

            // Create the chart
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Income',
                        data: values,
                        borderColor: 'rgba(54, 162, 235, 1)', // Blue for income
                        backgroundColor: 'rgba(54, 162, 235, 0.2)', // Light blue background                        
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                        },
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Date'
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Amount'
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
}

// Initialize the chart when the DOM content is loaded
document.addEventListener("DOMContentLoaded", initialiseIncomeChart);
