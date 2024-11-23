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
                "Content-Type": "application/json"  // Ensure correct content type
            },
            body: JSON.stringify(user)  // Stringify the JSON object
        })
        .then(response => {
            if (response.ok) {
                return response.json();  // Handle the JSON response
            } else {
                return response.text().then(text => {
                    throw new Error(text);  // If the response is not OK, handle it
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

    // Handle form submission for updating user details
    updateUserForm.addEventListener("submit", function (event) {
        event.preventDefault();

        // Fetch current user details when the user clicks the update button
        fetch("http://localhost:8080/api/users/me", {
            method: "GET",
            credentials: "include", // Ensures cookies are sent
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
            // Store user ID for future use
            localStorage.setItem("userId", user.userId);

            // Now update the user details
            const username = document.querySelector("#updateUsername").value;
            const email = document.querySelector("#updateEmail").value;
            const password = document.querySelector("#updatePassword").value;
            const passwordConfirm = document.querySelector("#updatePasswordConfirm").value;

            if (password !== passwordConfirm) {
                alert("Passwords do not match!");
                return;
            }

            // Retrieve user ID from localStorage
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

            // Send the updated user data to the server
            fetch(`http://localhost:8080/api/users/${userId}`, {
                method: "PUT",
                credentials: "include", // Ensures cookies are sent
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

    // Handle form submission for deleting user account
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const username = document.querySelector("#deleteUsername").value;
        const password = document.querySelector("#deletePassword").value;

        // Validate fields
        if (!username || !password) {
            alert("Please fill out all fields.");
            return;
        }

        // Fetch current user details when the delete form is submitted
        fetch("http://localhost:8080/api/users/me", {
            method: "GET",
            credentials: "include",  // Ensures cookies are sent with the request
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Unable to fetch user details");
            }
            return response.json();  // Extract the user details
        })
        .then(user => {
            console.log("Current User:", user);

            // Check if user.userId exists in the response
            if (!user.userId) {
                alert("User ID not found. Please log in again.");
                return;
            }

            // Store the userId in localStorage for future use
            localStorage.setItem("userId", user.userId);

            // Validate that the username entered matches the current user's username
            if (username !== user.username) {
                alert("Username does not match your registered username.");
                return;
            }

            // Retrieve the userId from localStorage
            const userId = localStorage.getItem("userId");

            if (!userId || userId === "undefined") {
                alert("User ID not found. Please log in again.");
                return;
            }

            const userToDelete = {
                username,
                password,
                userId: userId  // Use the userId retrieved from localStorage
            };

            // Send the DELETE request to the backend with the userId
            fetch(`http://localhost:8080/api/users/${userId}`, {
                method: "DELETE",  // Use DELETE request for account deletion
                headers: {
                    "Content-Type": "application/json"  // Set the content type as JSON
                },
                body: JSON.stringify(userToDelete)  // Send the username, password, and userId as JSON
            })
            .then(response => {
                if (response.ok) {
                    alert("Your account has been deleted successfully.");
                    window.location.href = "/login";  // Redirect to login page after deletion
                } else {
                    return response.text().then(text => {
                        throw new Error(text);  // Handle the error if deletion fails
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
            // Clear the error message if submission is successful
            errorMessageElement.textContent = "";
            alert("Category created successfully!");
            form.reset();
        })
        .catch(error => {
            // Display the error message in the HTML
            errorMessageElement.textContent = error.message;
        });
    });
});

//updating a category 
document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.querySelector("#categorySelect");
    const updateForm = document.querySelector("#updateCategoryForm");
    const errorMessageElement = document.querySelector("#errorMessageUpdateCategory");

    // Fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
        .then(response => response.json())
        .then(categories => {
            categories.forEach(category => {
                if (!category.categoryId) {
                    console.warn("Category ID is missing for:", category);
                    return; // Skip categories with missing IDs
                }
                const option = document.createElement("option");
                option.value = category.categoryId; // Use categoryId from API response
                option.textContent = category.name; // Display name
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error("Error fetching categories:", error));

    // Handle form submission
    updateForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const categoryId = categorySelect.value; // Selected category ID
        const newCategoryName = document.querySelector("#newCategoryName").value.trim();

        // Validate inputs
        if (!categoryId || categoryId === "undefined") {
            errorMessageElement.textContent = "Please select a valid category.";
            return;
        }
        if (!newCategoryName) {
            errorMessageElement.textContent = "Category name cannot be empty.";
            return;
        }

        // Make the PUT request
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

    // Fetch existing categories and populate the dropdown
    fetch("http://localhost:8080/api/categories/my-categories")
    .then(response => response.json())
    .then(categories => {
        categories.forEach(category => {
            if (!category.categoryId) {
                console.warn("Category ID is missing for:", category);
                return; // Skip categories with missing IDs
            }
            const option = document.createElement("option");
            option.value = category.categoryId; // Use categoryId from API response
            option.textContent = category.name; // Display name
            categorySelect.appendChild(option);
        });
    })
    .catch(error => console.error("Error fetching categories:", error));

    // Handle form submission
    deleteForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const categoryId = categorySelect.value; // Selected category ID

        // Validate inputs
        if (!categoryId) {
            errorMessageElement.textContent = "Please select a valid category.";
            return;
        }
        

        // Make the DELETE request
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
            // Check if response is empty
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

//get all categories 
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




