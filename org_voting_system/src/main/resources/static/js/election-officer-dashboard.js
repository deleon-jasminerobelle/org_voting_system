// Election Officer Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard
    initializeDashboard();

    // Set up event listeners
    setupEventListeners();
    
    // Add styles for messages
    addMessageStyles();
});

function initializeDashboard() {
    // Load initial dashboard content
    switchSection('dashboard');
}

function setupEventListeners() {
    // Add event listeners for dynamic elements
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('close-election-btn')) {
            e.preventDefault();
            const electionId = e.target.dataset.electionId;
            closeElection(electionId);
        }

        // Handle menu clicks
        if (e.target.closest('.menu a')) {
            e.preventDefault();
            const link = e.target.closest('.menu a');
            const section = link.dataset.section;
            switchSection(section);
        }
    });

    // Add sorting functionality
    const tableHeaders = document.querySelectorAll('#elections-table th[data-sort]');
    tableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const sortBy = this.dataset.sort;
            sortTable(sortBy);
        });
    });
}

function refreshElectionData() {
    // This could be enhanced with AJAX to fetch latest data
    // For now, just ensure the table is properly initialized
    updateTableStatus();
}

function closeElection(electionId) {
    if (confirm('Are you sure you want to close this election?')) {
        fetch(`/election-officer/election/${electionId}/close`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        })
        .then(response => {
            if (response.ok) {
                // Refresh the page or update the table
                location.reload();
            } else {
                alert('Failed to close election. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }
}

function sortTable(sortBy) {
    const table = document.getElementById('elections-table');
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));

    rows.sort((a, b) => {
        let aVal, bVal;

        switch(sortBy) {
            case 'title':
                aVal = a.cells[0].textContent.toLowerCase();
                bVal = b.cells[0].textContent.toLowerCase();
                break;
            case 'status':
                aVal = a.cells[1].textContent.toLowerCase();
                bVal = b.cells[1].textContent.toLowerCase();
                break;
            case 'startDate':
                aVal = new Date(a.cells[2].textContent);
                bVal = new Date(b.cells[2].textContent);
                break;
            case 'endDate':
                aVal = new Date(a.cells[3].textContent);
                bVal = new Date(b.cells[3].textContent);
                break;
            default:
                return 0;
        }

        if (aVal < bVal) return -1;
        if (aVal > bVal) return 1;
        return 0;
    });

    // Re-append sorted rows
    rows.forEach(row => tbody.appendChild(row));

    // Update sort indicators
    updateSortIndicators(sortBy);
}

function updateSortIndicators(activeSort) {
    const headers = document.querySelectorAll('#elections-table th[data-sort]');
    headers.forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
        if (header.dataset.sort === activeSort) {
            // Toggle sort direction (simplified - always ascending for now)
            header.classList.add('sort-asc');
        }
    });
}

function updateTableStatus() {
    const rows = document.querySelectorAll('#elections-table tbody tr');
    rows.forEach(row => {
        const statusCell = row.cells[1];
        const actionCell = row.cells[4];

        // Remove existing status classes
        statusCell.classList.remove('status-active', 'status-upcoming', 'status-closed');

        const statusText = statusCell.textContent.trim().toLowerCase();
        if (statusText === 'active') {
            statusCell.classList.add('status-active');
        } else if (statusText === 'upcoming') {
            statusCell.classList.add('status-upcoming');
        } else if (statusText === 'closed') {
            statusCell.classList.add('status-closed');
        }
    });
}

// Auto-refresh every 30 seconds
setInterval(refreshElectionData, 30000);

function switchSection(section) {
    // Update active menu item
    document.querySelectorAll('.menu a').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`.menu a[data-section="${section}"]`).classList.add('active');

    // Update page title and subtitle
    const pageTitle = document.getElementById('page-title');
    const pageSubtitle = document.getElementById('page-subtitle');

    switch(section) {
        case 'dashboard':
            pageTitle.textContent = 'Election Officer Dashboard';
            pageSubtitle.textContent = 'View elections and close active elections when necessary.';
            loadDashboardContent();
            break;
        case 'list-voters':
            pageTitle.textContent = 'List of Voters';
            pageSubtitle.textContent = 'View and manage registered voters.';
            loadListVotersContent();
            break;
        case 'list-elected':
            pageTitle.textContent = 'List of Elected';
            pageSubtitle.textContent = 'View elected candidates and results.';
            loadListElectedContent();
            break;
        case 'backup-restore':
            pageTitle.textContent = 'Backup & Restore';
            pageSubtitle.textContent = 'Backup system data and restore from backups.';
            loadBackupRestoreContent();
            break;
    }
}

function loadDashboardContent() {
    fetch('/election-officer/api/dashboard')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
            updateTableStatus();
        })
        .catch(error => {
            console.error('Error loading dashboard content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

function loadElectionManagementContent() {
    fetch('/election-officer/api/election-management')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading election management content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

function loadListVotersContent() {
    fetch('/election-officer/api/list-voters')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading list voters content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

function loadListElectedContent() {
    fetch('/election-officer/api/list-elected')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading list elected content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

function loadBackupRestoreContent() {
    fetch('/election-officer/api/backup-restore')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
            
            // Attach event listeners to backup and restore forms
            const backupForm = document.querySelector('form[action="/election-officer/backup"]');
            if (backupForm) {
                backupForm.addEventListener('submit', handleBackupSubmit);
            }
            
            const restoreForm = document.querySelector('form[action="/election-officer/restore"]');
            if (restoreForm) {
                restoreForm.addEventListener('submit', handleRestoreSubmit);
            }
        })
        .catch(error => {
            console.error('Error loading backup restore content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

function handleBackupSubmit(e) {
    e.preventDefault();
    
    const button = e.target.querySelector('button[type="submit"]');
    const originalText = button.textContent;
    button.textContent = 'Creating backup...';
    button.disabled = true;
    
    fetch('/election-officer/backup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    })
    .then(response => {
        if (response.ok) {
            showMessage('Database backup created successfully!', 'success');
            // Reload after showing message
            setTimeout(() => location.reload(), 2000);
        } else {
            showMessage('Failed to create backup. Please try again.', 'error');
            button.textContent = originalText;
            button.disabled = false;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('An error occurred: ' + error.message, 'error');
        button.textContent = originalText;
        button.disabled = false;
    });
}

function handleRestoreSubmit(e) {
    e.preventDefault();
    
    const fileInput = e.target.querySelector('input[name="file"]');
    if (!fileInput.files.length) {
        showMessage('Please select a file to restore', 'error');
        return;
    }
    
    const formData = new FormData(e.target);
    const button = e.target.querySelector('button[type="submit"]');
    const originalText = button.textContent;
    button.textContent = 'Restoring...';
    button.disabled = true;
    
    fetch('/election-officer/restore', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            showMessage('Database restored successfully!', 'success');
            // Reload after showing message
            setTimeout(() => location.reload(), 2000);
        } else {
            showMessage('Failed to restore backup. Please try again.', 'error');
            button.textContent = originalText;
            button.disabled = false;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('An error occurred: ' + error.message, 'error');
        button.textContent = originalText;
        button.disabled = false;
    });
}

function loadUserManagementContent() {
    fetch('/election-officer/api/user-management')
        .then(response => response.text())
        .then(html => {
            document.getElementById('content').innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading user management content:', error);
            document.getElementById('content').innerHTML = '<p>Error loading content. Please try again.</p>';
        });
}

// Message display functions
function addMessageStyles() {
    if (document.getElementById('message-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'message-styles';
    style.textContent = `
        .message-container {
            position: fixed;
            top: 20px;
            right: 20px;
            max-width: 400px;
            padding: 16px 20px;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 9999;
            animation: slideIn 0.3s ease-out;
            font-weight: 500;
        }
        
        .message-container.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .message-container.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .message-container.info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        
        @keyframes slideIn {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOut {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(400px);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

function showMessage(message, type = 'info', duration = 5000) {
    const container = document.createElement('div');
    container.className = `message-container ${type}`;
    container.textContent = message;
    document.body.appendChild(container);
    
    // Auto-remove message after duration
    setTimeout(() => {
        container.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => container.remove(), 300);
    }, duration);
}
