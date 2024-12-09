/*!
    * Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2023 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
    // 
// Scripts
// 

//navbar 
window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
 
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});

/* USERS */
//register a new user 
document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.querySelector("#registerForm");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const username = document.querySelector("#inputUsername").value;
        const email = document.querySelector("#inputEmail").value;
        const password = document.querySelector("#inputPassword").value;
        const passwordConfirm = document.querySelector("#inputPasswordConfirm").value;

        if (password !== passwordConfirm) {
            alert("Passwords do not match!");
            return;
        }

        const user = {
            username,
            email,
            password
        };

        fetch("http://localhost:8080/api/users/register-new", {
            method: "POST",
            headers: {
                "Content-Type": "application/json" 
            },
            body: JSON.stringify(user)  //stringify the JSON object
        })
        .then(response => {
            if (response.ok) {
                return response.json();  //handle the JSON response
            } else {
                return response.text().then(text => {
                    throw new Error(text);  //error handling if response  is not ok
                });
            }
        })
        .then(data => {
            alert("User registered successfully!");
            window.location.href = "http://localhost:8080/login";
        })
        .catch(error => {
            alert("Error during registration: " + error.message);
        });
    });
});

//update user details 
document.addEventListener("DOMContentLoaded", function () {
    const updateUserForm = document.querySelector("#updateUserForm");

    //handle form submission for updating user details
    updateUserForm.addEventListener("submit", function (event) {
        event.preventDefault();

        //fetch current user details when the user clicks the update button
        fetch("http://localhost:8080/api/users/me", {
            method: "GET",
            credentials: "include", //ensures cookies are sent
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch user details");
            }
            return response.json();
        })
        .then(user => {
            console.log("Current User:", user);
            //store user ID for future use
            localStorage.setItem("userId", user.userId);

            //update the user details
            const username = document.querySelector("#updateUsername").value;
            const email = document.querySelector("#updateEmail").value;
            const password = document.querySelector("#updatePassword").value;
            const passwordConfirm = document.querySelector("#updatePasswordConfirm").value;

            if (password !== passwordConfirm) {
                alert("Passwords do not match!");
                return;
            }

            //retrieve user ID from localStorage
            const userId = localStorage.getItem("userId");
            if (!userId) {
                alert("User ID not found. Please log in again.");
                return;
            }

            const updatedUser = {
                username,
                email,
                password
            };

            //send the updated user data to the server
            fetch(`http://localhost:8080/api/users/${userId}`, {
                method: "PUT",
                credentials: "include", 
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(updatedUser)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text);
                    });
                }
                return response.json();
            })
            .then(data => {
                alert("User updated successfully!");
                console.log("Updated User:", data);
            })
            .catch(error => {
                console.error("Error updating user:", error);
                alert("Failed to update user details: " + error.message);
            });
        })
        .catch(error => {
            console.error("Error fetching current user:", error);
            alert("Unable to load user details. Please log in again.");
        });
    });
});

//delete a user 
document.addEventListener("DOMContentLoaded", function () {
    const deleteForm = document.querySelector("#deleteAccountForm");

    //handle form submission for deleting user account
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const username = document.querySelector("#deleteUsername").value;
        const password = document.querySelector("#deletePassword").value;

        //validate fields
        if (!username || !password) {
            alert("Please fill out all fields.");
            return;
        }

        //fetch current user details when the delete form is submitted
        fetch("http://localhost:8080/api/users/me", {
            method: "GET",
            credentials: "include",  
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Unable to fetch user details");
            }
            return response.json();  //extract the user details
        })
        .then(user => {
            console.log("Current User:", user);

            //check if user.userId exists in the response
            if (!user.userId) {
                alert("User ID not found. Please log in again.");
                return;
            }

            //store the userId in localStorage for future use
            localStorage.setItem("userId", user.userId);

            //validate that the username entered matches the current user's username
            if (username !== user.username) {
                alert("Username does not match your registered username.");
                return;
            }

            //retrieve the userId from localStorage
            const userId = localStorage.getItem("userId");

            if (!userId || userId === "undefined") {
                alert("User ID not found. Please log in again.");
                return;
            }

            const userToDelete = {
                username,
                password,
                userId: userId  //use the userId retrieved from localStorage
            };

            //send the DELETE request to the backend with the userId
            fetch(`http://localhost:8080/api/users/${userId}`, {
                method: "DELETE",  
                headers: {
                    "Content-Type": "application/json" 
                },
                body: JSON.stringify(userToDelete)  //send the username, password, and userId as JSON
            })
            .then(response => {
                if (response.ok) {
                    alert("Your account has been deleted successfully.");
                    window.location.href = "/login";  //redirect to login page after deletion
                } else {
                    return response.text().then(text => {
                        throw new Error(text);  //handle the error if deletion fails
                    });
                }
            })
            .catch(error => {
                alert("Error deleting your account: " + error.message);
            });
        })
        .catch(error => {
            alert("Error fetching user details: " + error.message);
        });
    });
});

/* TRANSACTIONS */

//transaction charts 
//Expenses chart
document.addEventListener("DOMContentLoaded", function initialiseExpensesChart() {
    const ctx = document.getElementById('expensesChart').getContext('2d');

    //fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            //filter data to include only transactions of type 'EXPENSE'
            const expenseData = data.filter(transaction => transaction.type === 'EXPENSE');

            //sort filtered data by date (oldest first)
            expenseData.sort((a, b) => new Date(a.date) - new Date(b.date));

            //process data into labels and values for the chart
            const labels = expenseData.map(transaction => new Date(transaction.date).toLocaleDateString());
            const values = expenseData.map(transaction => transaction.amount); //replace with actual amount property

            //create the chart in Chart.js
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Expenses',
                        data: values,
                        borderColor: 'rgba(255, 99, 132, 1)', //red for expenses
                        backgroundColor: 'rgba(255, 99, 132, 0.2)', //light red background
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
});

//Income chart
document.addEventListener("DOMContentLoaded", function initialiseIncomeChart() {
    const ctx = document.getElementById('incomeChart').getContext('2d');

    //fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            //filter data to include only transactions of type 'INCOME'
            const incomeData = data.filter(transaction => transaction.type === 'INCOME');

            //sort filtered data by date (oldest first)
            incomeData.sort((a, b) => new Date(a.date) - new Date(b.date));

            //process data into labels and values for the chart
            const labels = incomeData.map(transaction => new Date(transaction.date).toLocaleDateString());
            const values = incomeData.map(transaction => transaction.amount); 

            //create the chart
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Income',
                        data: values,
                        borderColor: 'rgba(54, 162, 235, 1)', //blue for income
                        backgroundColor: 'rgba(54, 162, 235, 0.2)', //light blue background                        
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
                            },
                            beginAtZero: true
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
});

//Recent Transactions table
document.addEventListener("DOMContentLoaded", function () {
    //fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#transactionsTable tbody");

            //sort data by date in descending order (newest first)
            data = data.sort((a, b) => new Date(b.date) - new Date(a.date));

            //clear any existing rows 
            tableBody.innerHTML = "";

            //populate the table
            data.forEach(transaction => {
                const row = document.createElement("tr");

                //create cells for the row
                const dateCell = document.createElement("td");
                dateCell.textContent = new Date(transaction.date).toLocaleDateString(); //format date

                const categoryCell = document.createElement("td");
                categoryCell.textContent = transaction.categoryName.charAt(0).toUpperCase() + transaction.categoryName.slice(1).toLowerCase();

                const typeCell = document.createElement("td");
                typeCell.textContent = transaction.type;

                const amountCell = document.createElement("td");
                amountCell.textContent = `€${transaction.amount.toFixed(2)}`; //format amount with 2 decimals & include euro sign 

                const descriptionCell = document.createElement("td");
                descriptionCell.textContent = transaction.description || "No Description";

                typeCell.style.color = transaction.type === "INCOME" ? "green" : "red";

                //append cells to the row
                row.appendChild(dateCell);
                row.appendChild(categoryCell);
                row.appendChild(typeCell);
                row.appendChild(amountCell);
                row.appendChild(descriptionCell);

                //append the row to the table body
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
});

//expense/category pie chart 
document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById('categoryChart').getContext('2d');

    //fetch the transactions data
    fetch('http://localhost:8080/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            //group transactions by category
            const categoryData = data.reduce((acc, transaction) => {
                if (transaction.type === 'EXPENSE') { //filter by expense type 
                    acc[transaction.categoryName] = (acc[transaction.categoryName] || 0) + transaction.amount;
                }
                return acc;
            }, {});

            //extract category names and values 
            const labels = Object.keys(categoryData);
            const values = Object.values(categoryData);

            //define colors for each category
            const backgroundColors = [
                'rgba(255, 99, 132, 0.2)', //red
                'rgba(54, 162, 235, 0.2)', //blue
                'rgba(255, 206, 86, 0.2)', //yellow
                'rgba(75, 192, 192, 0.2)', //green
                'rgba(153, 102, 255, 0.2)', //purple
                'rgba(255, 159, 64, 0.2)'  //orange
            ];
            const borderColors = [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ];

            //create the pie chart
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Transaction Split by Category',
                        data: values,
                        backgroundColor: backgroundColors.slice(0, labels.length),
                        borderColor: borderColors.slice(0, labels.length),
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                        },
                        tooltip: {
                            callbacks: {
                                label: function (tooltipItem) {
                                    const category = tooltipItem.label;
                                    const amount = tooltipItem.raw;
                                    return `${category}: $${amount.toFixed(2)}`;
                                }
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
});

//create transaction 
document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("#createTransactionForm");
    const categoryDropdown = document.querySelector("#createTransactionCategory");
    const errorMessageElement = document.querySelector("#errorMessageCreateTransaction");

    //fetch categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch categories.");
            }
            return response.json();
        })
        .then(categories => {
            //clear existing options
            categoryDropdown.innerHTML = "";

            //populate dropdown with fetched categories
            categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.categoryId; // Use `categoryId` for the value
                option.textContent = category.name; // Use `name` for the display text
                categoryDropdown.appendChild(option);
            });
        })
        .catch(error => {
            console.error("Error fetching categories:", error);
            categoryDropdown.innerHTML = `<option value="" disabled>Error loading categories</option>`;
        });

    //form submission logic
    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);

        //debugging: Log the description field
        const description = formData.get("createTransactionDescription");
        console.log("Description Value:", description);

        const transactionData = {
            category: {
                categoryId: parseInt(formData.get("createTransactionCategory"), 10), //ensure categoryId is an integer
            },
            amount: parseFloat(formData.get("createTransactionAmount")).toFixed(2), //format to two decimal places
            date: formData.get("createTransactionDate"),
            description: description || "", //default to empty string if null
            type: formData.get("createTransactionType"),
        };

        console.log("Transaction Data:", transactionData); //debug full data object

        fetch("http://localhost:8080/api/transactions", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(transactionData),
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || "An unexpected error occurred.");
                    });
                }
                return response.json();
            })
            .then(data => {
                errorMessageElement.textContent = "";
                alert("Transaction created successfully!");
                form.reset();
            })
            .catch(error => {
                errorMessageElement.textContent = error.message;
            });
    });
});

//update transaction
document.addEventListener("DOMContentLoaded", function () {
    const tableBody = document.querySelector("#updateTransactionsTable tbody");
    const form = document.querySelector("#updateTransactionForm");
    const categoryDropdown = document.querySelector("#updateTransactionCategory");
    const errorMessageElement = document.querySelector("#errorMessageUpdateTransaction");

    //fetch transactions and populate the table
    fetch("http://localhost:8080/api/transactions/my-transactions")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch transactions.");
            }
            return response.json();
        })
        .then(transactions => {
            tableBody.innerHTML = ""; //clear the table body
            //sort the transactions
            transactions.sort((a, b) => new Date(b.date) - new Date(a.date));
            transactions.forEach(transaction => {
                const row = document.createElement("tr");

                //create table cells
                row.innerHTML = `
                    <td>${transaction.categoryName}</td>
                    <td>€${transaction.amount.toFixed(2)}</td>
                    <td>${new Date(transaction.date).toLocaleDateString()}</td>
                    <td>${transaction.description || "No Description"}</td>
                    <td>${transaction.type}</td>
                    <td><button class="btn btn-primary btn-sm" data-id="${transaction.transactionId}">Update</button></td>
                `;

                //append the row to the table body
                tableBody.appendChild(row);
            });

            //add event listeners to the "Select" buttons
            document.querySelectorAll("button[data-id]").forEach(button => {
                button.addEventListener("click", function () {
                    const transactionId = this.getAttribute("data-id");
                    console.log("Selected Transaction ID:", transactionId); //debugging 
                
                    fetch(`http://localhost:8080/api/transactions/${transactionId}`)
                        .then(response => {
                            if (!response.ok) {
                                throw new Error("Failed to fetch transaction details.");
                            }
                            return response.json();
                        })
                        .then(transaction => {
                            console.log("Fetched Transaction Data:", transaction); //debugging 
                
                            //prefill the form fields
                            form.querySelector("#updateTransactionCategory").value = transaction.categoryId;
                            form.querySelector("#updateTransactionAmount").value = transaction.amount.toFixed(2);
                            form.querySelector("#updateTransactionDate").value = transaction.date;
                            form.querySelector("#updateTransactionDescription").value = transaction.description || "";
                            form.querySelector("#updateTransactionType").value = transaction.type;
                
                            //store the transaction ID for the update
                            form.setAttribute("data-transaction-id", transactionId);
                            console.log("Form data-transaction-id set to:", transactionId); //debugging 
                        })
                        .catch(error => {
                            console.error("Error fetching transaction details:", error);
                        });
                });
                
            });
        })
        .catch(error => {
            console.error("Error fetching transactions:", error);
        });

    //fetch categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch categories.");
            }
            return response.json();
        })
        .then(categories => {
            categoryDropdown.innerHTML = "";

            categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.categoryId;
                option.textContent = category.name;
                categoryDropdown.appendChild(option);
            });
        })
        .catch(error => {
            console.error("Error fetching categories:", error);
            categoryDropdown.innerHTML = `<option value="" disabled>Error loading categories</option>`;
        });

    //handle form submission for updates
    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const transactionId = form.getAttribute("data-transaction-id");

        if (!transactionId) {
            errorMessageElement.textContent = "No transaction selected for update.";
            return;
        }

        const updatedTransactionData = {
            category: {
                categoryId: parseInt(form.querySelector("#updateTransactionCategory").value, 10),
            },
            amount: parseFloat(form.querySelector("#updateTransactionAmount").value).toFixed(2),
            date: form.querySelector("#updateTransactionDate").value,
            description: form.querySelector("#updateTransactionDescription").value || "",
            type: form.querySelector("#updateTransactionType").value,
        };

        fetch(`http://localhost:8080/api/transactions/${transactionId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedTransactionData),
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || "An unexpected error occurred.");
                    });
                }
                return response.json();
            })
            .then(() => {
                errorMessageElement.textContent = "";
                alert("Transaction updated successfully!");
                form.reset();
                form.removeAttribute("data-transaction-id");
            })
            .catch(error => {
                errorMessageElement.textContent = error.message;
                console.error("Error updating transaction:", error);
            });
    });
});

//delete transaction
document.addEventListener("DOMContentLoaded", function () {
    const tableBody = document.querySelector("#deleteTransactionsTable tbody");
    const form = document.querySelector("#deleteTransactionForm");
    const errorMessageElement = document.querySelector("#errorMessageDeleteTransaction");

    let selectedTransactionId = null; //store the selected transaction ID for deletion

    //fetch and populate transactions
    fetch("http://localhost:8080/api/transactions/my-transactions")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch transactions.");
            }
            return response.json();
        })
        .then(transactions => {
            tableBody.innerHTML = ""; //clear any existing rows
            //sort the transactions newest first 
            transactions.sort((a, b) => new Date(b.date) - new Date(a.date));
            transactions.forEach(transaction => {
                const row = document.createElement("tr");

                //populate the table row
                row.innerHTML = `
                    <td>${transaction.categoryName}</td>
                    <td>€${transaction.amount.toFixed(2)}</td>
                    <td>${new Date(transaction.date).toLocaleDateString()}</td>
                    <td>${transaction.description || "No Description"}</td>
                    <td>${transaction.type}</td>
                    <td><button class="btn btn-danger btn-sm" data-id="${transaction.transactionId}">Delete</button></td>
                `;

                //append the row to the table body
                tableBody.appendChild(row);
            });

            //add event listeners to the "Select" buttons
            document.querySelectorAll("button[data-id]").forEach(button => {
                button.addEventListener("click", function () {
                    selectedTransactionId = this.getAttribute("data-id"); //store the selected ID
                });
            });
        })
        .catch(error => {
            console.error("Error fetching transactions:", error);
            errorMessageElement.textContent = "Failed to load transactions.";
        });

    //handle form submission for deleting the selected transaction
    form.addEventListener("submit", function (event) {
        event.preventDefault(); //prevent default form submission

        if (!selectedTransactionId) {
            errorMessageElement.textContent = "No transaction selected for deletion.";
            return;
        }

        fetch(`http://localhost:8080/api/transactions/${selectedTransactionId}`, {
            method: "DELETE",
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || "Failed to delete transaction.");
                    });
                }
                alert("Transaction deleted successfully!");
                //refresh the table after deletion
                location.reload();
            })
            .catch(error => {
                console.error("Error deleting transaction:", error);
                errorMessageElement.textContent = error.message;
            });
    });
});

/* CATEGORIES */
//create category
document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("#createCategoryForm");
    const errorMessageElement = document.querySelector("#errorMessage");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);
        const categoryData = {
            name: formData.get("categoryName"),
        };

        fetch("http://localhost:8080/api/categories", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(categoryData),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            //clear the error message if submission is successful
            errorMessageElement.textContent = "";
            alert("Category created successfully!");
            form.reset();
        })
        .catch(error => {
            //display the error message in the HTML
            errorMessageElement.textContent = error.message;
        });
    });
});

//updating a category 
document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.querySelector("#categorySelect");
    const updateForm = document.querySelector("#updateCategoryForm");
    const errorMessageElement = document.querySelector("#errorMessageUpdateCategory");

    //fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
        .then(response => response.json())
        .then(categories => {
            categories.forEach(category => {
                if (!category.categoryId) {
                    console.warn("Category ID is missing for:", category);
                    return; 
                }
                const option = document.createElement("option");
                option.value = category.categoryId; //use categoryId from API response
                option.textContent = category.name; //display name
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error("Error fetching categories:", error));

    //handle form submission
    updateForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const categoryId = categorySelect.value; //selected category ID
        const newCategoryName = document.querySelector("#newCategoryName").value.trim();

        //validate inputs
        if (!categoryId || categoryId === "undefined") {
            errorMessageElement.textContent = "Please select a valid category.";
            return;
        }
        if (!newCategoryName) {
            errorMessageElement.textContent = "Category name cannot be empty.";
            return;
        }

        //make the API call
        fetch(`http://localhost:8080/api/categories/${categoryId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name: newCategoryName }),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            alert("Category updated successfully!");
            errorMessageElement.textContent = "";
            updateForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//delete a category 
document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.querySelector("#categorySelectDelete");
    const deleteForm = document.querySelector("#deleteCategoryForm");
    const errorMessageElement = document.querySelector("#errorMessageDeleteCategory");

    //fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
    .then(response => response.json())
    .then(categories => {
        categories.forEach(category => {
            if (!category.categoryId) {
                console.warn("Category ID is missing for:", category);
                return; 
            }
            const option = document.createElement("option");
            option.value = category.categoryId; //use categoryId from API response
            option.textContent = category.name; //display name
            categorySelect.appendChild(option);
        });
    })
    .catch(error => console.error("Error fetching categories:", error));

    //handle form submission
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const categoryId = categorySelect.value; //selected category ID

        //validate inputs
        if (!categoryId) {
            errorMessageElement.textContent = "Please select a valid category.";
            return;
        }
        

        //make the DELETE call
        fetch(`http://localhost:8080/api/categories/${categoryId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            }
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            //check if response is empty
            if (response.status === 204) {
                return; // No content returned, so we don't need to process further
            }
            return response.json();
        })
        .then(data => {
            alert("Category deleted successfully!");
            errorMessageElement.textContent = "";
            deleteForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//get all categories for table 
fetch("http://localhost:8080/api/categories/my-categories")
    .then(response => response.json())
    .then(categories => {
        const categoryList = document.getElementById("categoryList");
        categories.forEach(category => {
            const listItem = document.createElement("li");
            listItem.classList.add("list-group-item");
            listItem.textContent = category.name;
            categoryList.appendChild(listItem);
        });
    })
    .catch(error => console.error("Error fetching categories:", error));


/* BUDGETS */
//create a budget 
document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("#createBudgetForm");
    const errorMessageElement = document.querySelector("#errorMessage");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);
        const budgetData = {
            name: formData.get("budgetName"),
            amount: formData.get("budgetAmount"),
            startDate: formData.get("budgetStartDate"),
            endDate: formData.get("budgetEndDate")
        };

        fetch("http://localhost:8080/api/budgets", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(budgetData),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            //clear the error message if submission is successful
            errorMessageElement.textContent = "";
            alert("Budget created successfully!");
            form.reset();
        })
        .catch(error => {
            //display the error message in the HTML
            errorMessageElement.textContent = error.message;
        });
    });
});

//updating a budget 
document.addEventListener("DOMContentLoaded", function () {
    const budgetSelect = document.querySelector("#budgetSelect");
    const updateForm = document.querySelector("#updateBudgetForm");
    const errorMessageElement = document.querySelector("#errorMessageUpdateBudget");

    //fetch existing budget and populate the dropdown
    fetch("http://localhost:8080/api/budgets/my-budgets")
        .then(response => response.json())
        .then(budgets => {
            budgets.forEach(budget => {
                if (!budget.budgetId) {
                    console.warn("Budget ID is missing for:", budget);
                    return; 
                }
                const option = document.createElement("option");
                option.value = budget.budgetId; //use budgetId from API response
                option.textContent = budget.name; //display name
                budgetSelect.appendChild(option);
            });
        })
        .catch(error => console.error("Error fetching budgets:", error));

    //handle form submission
    updateForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const budgetId = budgetSelect.value; //selected budget ID
        const newBudgetName = document.querySelector("#updateBudgetName").value.trim();
        const newBudgetAmount = document.querySelector("#updateBudgetAmount").value.trim();
        const newBudgetStartDate = document.querySelector("#updateBudgetStartDate").value.trim();
        const newBudgetEndDate = document.querySelector("#updateBudgetEndDate").value.trim();

        //validate inputs
        if (!budgetId || budgetId === "undefined") {
            errorMessageElement.textContent = "Please select a valid budget.";
            return;
        }
        if (!newBudgetName) {
            errorMessageElement.textContent = "Budget name cannot be empty.";
            return;
        }

        //make the PUT call
        fetch(`http://localhost:8080/api/budgets/${budgetId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name: newBudgetName, amount: newBudgetAmount, startDate: newBudgetStartDate, endDate: newBudgetEndDate }),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            alert("Budget updated successfully!");
            errorMessageElement.textContent = "";
            updateForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//deleting a budget 
document.addEventListener("DOMContentLoaded", function () {
    const budgetSelect = document.querySelector("#budgetSelectDelete");
    const deleteForm = document.querySelector("#deleteBudgetForm");
    const errorMessageElement = document.querySelector("#errorMessageDeleteBudget");

    //fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/budgets/my-budgets")
    .then(response => response.json())
    .then(budgets => {
        budgets.forEach(budget => {
            if (!budget.budgetId) {
                console.warn("Budget ID is missing for:", budget);
                return;
            }
            const option = document.createElement("option");
            option.value = budget.budgetId; //use budgetId from API response
            option.textContent = budget.name; //display name
            budgetSelect.appendChild(option);
        });
    })
    .catch(error => console.error("Error fetching budgets:", error));

    //handle form submission
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const budgetId = budgetSelect.value; //selected budget ID

        //validate 
        if (!budgetId) {
            errorMessageElement.textContent = "Please select a valid budget.";
            return;
        }
        

        //make the DELETE API call
        fetch(`http://localhost:8080/api/budgets/${budgetId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            }
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            //check if response is empty
            if (response.status === 204) {
                return; // No content returned, so we don't need to process further
            }
            return response.json();
        })
        .then(data => {
            alert("Budget deleted successfully!");
            errorMessageElement.textContent = "";
            deleteForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//budget charts 
document.addEventListener("DOMContentLoaded", function () {
    const apiUrl = "http://localhost:8080/api/budgets/my-budgets";
    const tableBody = document.getElementById("budgetTableBody");

    fetch(apiUrl)
        .then(response => response.json())
        .then(budgets => {
            //sort budgets by start date
            budgets.sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
            
            //populate the table with budget data
            budgets.forEach(budget => {
                const row = document.createElement("tr");

                const budgetNameCell = document.createElement("td");
                budgetNameCell.textContent = budget.name || `Budget ${budget.budgetId}`;
                row.appendChild(budgetNameCell);

                const amountCell = document.createElement("td");
                amountCell.textContent = `€${budget.amount.toFixed(2)}`;
                row.appendChild(amountCell);

                const startDateCell = document.createElement("td");
                const formattedStartDate = new Date(budget.startDate).toLocaleDateString("en-US", { year: 'numeric', month: 'short', day: 'numeric' });
                startDateCell.textContent = formattedStartDate;
                row.appendChild(startDateCell);

                const endDateCell = document.createElement("td");
                const formattedEndDate = new Date(budget.endDate).toLocaleDateString("en-US", { year: 'numeric', month: 'short', day: 'numeric' });
                endDateCell.textContent = formattedEndDate;
                row.appendChild(endDateCell);

                //calculate weekly spending limit
                const weeklyLimitCell = document.createElement("td");
                const startDate = new Date(budget.startDate);
                const endDate = new Date(budget.endDate);

                //calculate the number of weeks
                const millisecondsPerWeek = 7 * 24 * 60 * 60 * 1000; // 7 days
                const weeks = Math.max(1, Math.ceil((endDate - startDate) / millisecondsPerWeek));

                //calculate the weekly spending limit
                const weeklyLimit = budget.amount / weeks;
                weeklyLimitCell.textContent = `€${weeklyLimit.toFixed(2)}`;
                row.appendChild(weeklyLimitCell);

                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error fetching budgets:", error);
        });
});

/* GOALS */
//create a goal 
document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("#createGoalForm");
    const errorMessageElement = document.querySelector("#errorMessageCreateGoal");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);
        const goalData = {
            name: formData.get("createGoalName"),
            targetAmount: formData.get("createTargetAmount"),
            currentAmount: formData.get("createCurrentAmount"),
            deadline: formData.get("createDeadline"),
            status: "IN_PROGRESS"
        };

        fetch("http://localhost:8080/api/goals", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(goalData),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            //clear the error message if submission is successful
            errorMessageElement.textContent = "";
            alert("Goal created successfully!");
            form.reset();
        })
        .catch(error => {
            //display the error message in the HTML
            errorMessageElement.textContent = error.message;
        });
    });
});

//updating a goal 
document.addEventListener("DOMContentLoaded", function () {
    const goalSelect = document.querySelector("#goalSelect");
    const updateForm = document.querySelector("#updateGoalForm");
    const errorMessageElement = document.querySelector("#errorMessageDeleteBudget");

    //fetch existing budget and populate the dropdown
    fetch("http://localhost:8080/api/goals/my-goals")
        .then(response => response.json())
        .then(goals => {
            goals.forEach(goal => {
                if (!goal.goalId) {
                    console.warn("Goal ID is missing for:", goal);
                    return; 
                }
                const option = document.createElement("option");
                option.value = goal.goalId; //use goalId from API response
                option.textContent = goal.goalName; //display name
                goalSelect.appendChild(option);
            });
        })
        .catch(error => console.error("Error fetching goals:", error));

    //handle form submission
    updateForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const goalId = goalSelect.value; //selected goal ID
        const newGoalName = document.querySelector("#updateGoalName").value.trim();
        const newTargetAmount = document.querySelector("#updateGoalTargetAmount").value.trim();
        const newCurrentAmount = document.querySelector("#updateGoalCurrentAmount").value.trim();
        const newDeadline = document.querySelector("#updateDeadline").value.trim();
        const newStatus = document.querySelector("#status").value.trim();

        //validate
        if (!goalId || goalId === "undefined") {
            errorMessageElement.textContent = "Please select a valid budget.";
            return;
        }
        if (!newGoalName) {
            errorMessageElement.textContent = "Goal name cannot be empty.";
            return;
        }

        //make the PUT call
        fetch(`http://localhost:8080/api/goals/${goalId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name: newGoalName, targetAmount: newTargetAmount, currentAmount: newCurrentAmount, deadline: newDeadline, status: newStatus}),
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            return response.json();
        })
        .then(data => {
            alert("Goal updated successfully!");
            errorMessageElement.textContent = "";
            updateForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//deleting a goal 
document.addEventListener("DOMContentLoaded", function () {
    const goalSelect = document.querySelector("#goalSelectDelete");
    const deleteForm = document.querySelector("#deleteGoalForm");
    const errorMessageElement = document.querySelector("#errorMessageDeleteGoal");

    //fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/goals/my-goals")
    .then(response => response.json())
    .then(goals => {
        goals.forEach(goal => {
            if (!goal.goalId) {
                console.warn("Goal ID is missing for:", goal);
                return; 
            }
            const option = document.createElement("option");
            option.value = goal.goalId; //use goalId from API response
            option.textContent = goal.goalName; 
            goalSelect.appendChild(option);
        });
    })
    .catch(error => console.error("Error fetching goals:", error));

    //form submission
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const goalId = goalSelect.value; //selected goal ID

        //validate 
        if (!goalId) {
            errorMessageElement.textContent = "Please select a valid goal.";
            return;
        }
        

        //DELETE request
        fetch(`http://localhost:8080/api/goals/${goalId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            }
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || "An unexpected error occurred.");
                });
            }
            //check if response is empty
            if (response.status === 204) {
                return; // No content returned, so we don't need to process further
            }
            return response.json();
        })
        .then(data => {
            alert("Goal deleted successfully!");
            errorMessageElement.textContent = "";
            deleteForm.reset();
        })
        .catch(error => {
            errorMessageElement.textContent = error.message;
        });
    });
});

//goal table
document.addEventListener("DOMContentLoaded", function () {
    const apiUrl = "http://localhost:8080/api/goals/my-goals";
    const tableBody = document.getElementById("goalTableBody");

    fetch(apiUrl)
        .then(response => response.json())
        .then(goals => {
            //sort goals by deadline
            goals.sort((a, b) => new Date(a.deadline) - new Date(b.deadline));

            //populate the table with goal data
            goals.forEach(goal => {
                const row = document.createElement("tr");

                //goal name
                const goalNameCell = document.createElement("td");
                goalNameCell.textContent = goal.goalName || `Goal ${goal.goalId}`;
                row.appendChild(goalNameCell);

                //target amount
                const targetAmountCell = document.createElement("td");
                const targetAmount = parseFloat(goal.targetAmount);
                targetAmountCell.textContent = `€${targetAmount.toFixed(2)}`;
                row.appendChild(targetAmountCell);

                //current amount
                const currentAmountCell = document.createElement("td");
                const currentAmount = parseFloat(goal.currentAmount);
                currentAmountCell.textContent = `€${currentAmount.toFixed(2)}`;
                row.appendChild(currentAmountCell);

                //deadline
                const deadlineCell = document.createElement("td");
                const formattedDeadline = new Date(goal.deadline).toLocaleDateString("en-US", { year: 'numeric', month: 'short', day: 'numeric' });
                deadlineCell.textContent = formattedDeadline;
                row.appendChild(deadlineCell);

                //status
                const statusCell = document.createElement("td");
                const statusMapping = {
                    IN_PROGRESS: "In Progress",
                    ACHIEVED: "Achieved"
                };
                statusCell.textContent = statusMapping[goal.status] || "Unknown";
                row.appendChild(statusCell);

                //append row to the table
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error fetching goals:", error);
        });
});

//goal charts 
document.addEventListener("DOMContentLoaded", function () {
    const apiUrl = "http://localhost:8080/api/goals/my-goals";
    const ctx = document.getElementById("goalsProgressChart").getContext("2d");

    fetch(apiUrl)
        .then(response => response.json())
        .then(goals => {
            //prepare data for the chart
            const goalNames = goals.map(goal => goal.goalName || `Goal ${goal.goalId}`);
            const currentAmounts = goals.map(goal => parseFloat(goal.currentAmount) || 0);
            const targetAmounts = goals.map(goal => parseFloat(goal.targetAmount) || 0);

            //create the chart
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: goalNames, //goal names as labels
                    datasets: [
                        {
                            label: 'Current Amount (€)',
                            data: currentAmounts,
                            backgroundColor: 'rgba(75, 192, 192, 0.6)', //light blue
                            borderColor: 'rgba(75, 192, 192, 1)', //dark blue
                            borderWidth: 1
                        },
                        {
                            label: 'Target Amount (€)',
                            data: targetAmounts,
                            backgroundColor: 'rgba(255, 99, 132, 0.6)', //light red
                            borderColor: 'rgba(255, 99, 132, 1)', //dark red
                            borderWidth: 1
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        tooltip: {
                            callbacks: {
                                label: (tooltipItem) => {
                                    return `€${tooltipItem.raw.toFixed(2)}`;
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Goals'
                            }
                        },
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Amount (€)'
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Error fetching goals:", error));
});

//external data sources 
//currency converter 
async function convertCurrency() {
    const amount = document.getElementById("amount").value;
    const baseCurrency = document.getElementById("baseCurrency").value;
    const targetCurrency = document.getElementById("targetCurrency").value;

    if (!amount || amount <= 0) {
      alert("Please enter a valid amount.");
      return;
    }

    try {
      const response = await fetch(`https://api.frankfurter.app/latest?amount=${amount}&from=${baseCurrency}&to=${targetCurrency}`);
      if (!response.ok) {
        throw new Error("Failed to fetch conversion rates.");
      }

      const data = await response.json();
      const convertedAmount = data.rates[targetCurrency];

      document.getElementById("result").innerText = 
        `${amount} ${baseCurrency} = ${convertedAmount.toFixed(2)} ${targetCurrency}`;
    } catch (error) {
      console.error(error);
      alert("Error fetching conversion data. Please try again later.");
    }
  }

  //financial news API
  async function loadFinancialNews() {
    const apiKey = "03a5fbb0a42746f1b1c58d90c4dbd65a";
    const url = `https://newsapi.org/v2/everything?q=finance&apiKey=${apiKey}`;
  
    try {
      const response = await fetch(url);
      const data = await response.json();
  
      const newsWidget = document.getElementById("news-widget");
  
      data.articles.slice(0, 6).forEach(article => {
        const newsCard = `
          <div class="col-md-4 mb-4">
            <div class="card h-100">
              <img src="${article.urlToImage || 'https://via.placeholder.com/150'}" class="card-img-top" alt="${article.title}">
              <div class="card-body">
                <h5 class="card-title">${article.title}</h5>
                <p class="card-text">${article.description || 'No description available.'}</p>
                <a href="${article.url}" class="btn btn-primary" target="_blank">Read More</a>
              </div>
            </div>
          </div>`;
        
        newsWidget.insertAdjacentHTML('beforeend', newsCard);
      });
    } catch (error) {
      console.error("Error fetching financial news:", error);
      document.getElementById("news-widget").innerHTML = `<p class="text-danger">Failed to load news. Please try again later.</p>`;
    }
  }
  
  //load news on page load
  document.addEventListener("DOMContentLoaded", loadFinancialNews);
  