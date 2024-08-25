function acceptRequest(userId) {
        $.ajax({
            url: `/api/users/accept/${userId}`,
            type: 'POST',
            success: function() {
                location.reload();
            },
            error: function(err) {
                console.error('Error accepting request:', err.responseText);
                alert('Failed to accept request.');
            }
        });
    }

    function declineRequest(userId) {
        $.ajax({
            url: `/api/users/decline/${userId}`,
            type: 'POST',
            success: function() {
                location.reload();
            },
            error: function(err) {
                console.error('Error declining request:', err.responseText);
                alert('Failed to decline request.');
            }
        });
    }


function showFollowersModal(userId) {
$.ajax({
    url: '/api/users/' + userId + '/followers',
    method: 'GET',
    success: function(data) {
        if (Array.isArray(data)) {
            let followersHtml = data.map(user =>
                `<div class="user-item">
                    <img src="${user.image}" alt="User Image" class="img-thumbnail">
                    <div>
                        <p>${user.username}</p>
                    </div>
                </div>`).join('');
            $('#followersList').html(followersHtml);
        } else {
            $('#followersList').html('<p>No followers found.</p>');
        }
        $('#followersModal').modal('show');
    },
    error: function() {
        $('#followersList').html('<p>Error loading followers.</p>');
        $('#followersModal').modal('show');
    }
});
}


    function showPostModal(postId) {
    $.ajax({
        url: `/api/posts/${postId}`,
        method: 'GET',
        success: function(post) {
            // Build the HTML content for the modal
            const postHtml = `
                <div class="post-header">
                    <img src="${post.user.image}" class="rounded-circle mr-3" style="width: 40px; height: 40px;"/>
                    <a href="/api/users/viewProfile/${post.user.username}" class="username">${post.user.username}</a>
                </div>
                <img src="${post.imagePost}" alt="Post Image" class="img-fluid"/>
                <p>${post.caption}</p>
                <div class="actions">
                    <span id="likeButton"
                          onclick="toggleLike(${post.id})"
                          style="${post.liked ? 'display:none;' : 'display:block;'}">
                        <i class="fa-regular fa-heart"></i>
                    </span>
                    <span id="likeButton"
                          onclick="toggleLike(${post.id})"
                          style="${post.liked ? 'display:block;' : 'display:none;'}">
                        <i class="fa-solid fa-heart"></i>
                    </span>
                </div>
            `;

            $('#postContent').html(postHtml);
            $('#postModal').modal('show');
        },
        error: function() {
            $('#postContent').html('<p>Error loading post details.</p>');
            $('#postModal').modal('show');
        }
    });
}

