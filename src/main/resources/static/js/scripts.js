/*!
    * Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2023 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
    // 
// Scripts
// 

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
        })
        .catch(error => {
            alert("Error during registration: " + error.message);
        });
    });
});


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




