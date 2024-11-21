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

// Wait for the DOM to be fully loaded before adding event listeners
document.addEventListener("DOMContentLoaded", function () {
    // Access the form element
    const form = document.getElementById('categoryForm');
    
    // Add event listener for form submission
    form.addEventListener('submit', function (event) {
        // Prevent the form from submitting the default way (refresh)
        event.preventDefault();

        // Get the category name from the form input field
        const categoryName = document.getElementById('categoryName').value;

        // Prepare the data to send in the POST request
        const categoryData = {
            name: categoryName
        };

        // Make the POST request to create the category
        fetch('http://localhost:8080/api/categories', {
            method: 'POST', 
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(categoryData) // Convert data to JSON
        })
        .then(response => response.json()) // Parse the response as JSON
        .then(data => {
            // Display feedback message based on the response
            if (data && data.name) {
                document.getElementById('feedback').textContent = `Category "${data.name}" created successfully!`;
            } else {
                document.getElementById('feedback').textContent = 'Failed to create category.';
            }
        })
        .catch(error => {
            // Catch any errors (e.g., network issues)
            console.error('Error creating category:', error);
            document.getElementById('feedback').textContent = 'Error creating category.';
        });
    });
});
