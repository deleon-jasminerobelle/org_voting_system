function showTab(tabId, element) {
    // Hide all tab contents
    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.classList.remove("active");
    });

    // Remove active class from all nav items
    document.querySelectorAll(".nav-item").forEach(item => {
        item.classList.remove("active");
    });

    // Show selected tab content
    document.getElementById(tabId).classList.add("active");
    element.classList.add("active");
}

function castVote(candidateName, position, organization) {
    if (confirm(`Are you sure you want to vote for ${candidateName} as ${position} of ${organization}?`)) {
        alert(`Your vote for ${candidateName} as ${position} of ${organization} has been submitted!`);
        
        // Update the button to show voted
        event.target.innerHTML = '<i class="fas fa-check"></i> Voted';
        event.target.style.background = '#10b981';
        event.target.disabled = true;
        
        // Switch to voting status tab after voting
        setTimeout(() => {
            const statusNavItem = document.querySelector('.nav-item:nth-child(4)');
            showTab('voting-status', statusNavItem);
        }, 1500);
    }
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Add any initialization code here
    console.log('Voter Dashboard loaded');
    
    // Ensure home tab is active by default
    const homeTab = document.getElementById('home');
    const homeNavItem = document.querySelector('.nav-item');
    if (homeTab && homeNavItem) {
        homeTab.classList.add('active');
        homeNavItem.classList.add('active');
    }
});
