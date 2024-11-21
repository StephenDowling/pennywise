document.addEventListener("DOMContentLoaded", function () {
    // Fetch the transactions data
    fetch('/api/transactions/my-transactions')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector("#transactionsTable tbody");

            // Sort data by date in descending order (newest first)
            data = data.sort((a, b) => new Date(b.date) - new Date(a.date));

            // Clear any existing rows (if re-rendering is needed)
            tableBody.innerHTML = "";

            // Populate the table
            data.forEach(transaction => {
                const row = document.createElement("tr");

                // Create cells for the row
                const dateCell = document.createElement("td");
                dateCell.textContent = new Date(transaction.date).toLocaleDateString(); // Format date

                const categoryCell = document.createElement("td");
                categoryCell.textContent = transaction.categoryName.charAt(0).toUpperCase() + transaction.categoryName.slice(1).toLowerCase();

                const typeCell = document.createElement("td");
                typeCell.textContent = transaction.type;

                const amountCell = document.createElement("td");
                amountCell.textContent = `€${transaction.amount.toFixed(2)}`; // Format amount with 2 decimals

                const descriptionCell = document.createElement("td");
                descriptionCell.textContent = transaction.description || "No Description";

                typeCell.style.color = transaction.type === "INCOME" ? "green" : "red";

                // Append cells to the row
                row.appendChild(dateCell);
                row.appendChild(categoryCell);
                row.appendChild(typeCell);
                row.appendChild(amountCell);
                row.appendChild(descriptionCell);

                // Append the row to the table body
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching transactions:', error));
});
