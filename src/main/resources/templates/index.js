function showPostModal(postId) {
    $.ajax({
        url: `/api/posts/${postId}`,
        method: 'GET',
        success: function(post) {
            // Build the HTML content for the modal
            const postHtml = `
                <div class="post-header">
                    <img src="${post.user.image}" class="rounded-circle mr-3" style="width: 40px; height: 40px;" />
                    <a href="/api/users/viewProfile/${post.user.username}" class="username">${post.user.username}</a>
                </div>
                <img src="${post.imagePost}" alt="Post Image" class="img-fluid" />
                <p>${post.caption}</p>
                <div class="actions">
                    <span id="likeButtonUnlike" class="${post.liked ? 'd-none' : 'd-inline'}">
                        <i class="fa-regular fa-heart"></i>
                    </span>
                    <span id="likeButtonLike" class="${post.liked ? 'd-inline' : 'd-none'}">
                        <i class="fa-solid fa-heart"></i>
                    </span>
                </div>`;
            $('#postContent').html(postHtml);
            $('#postModal').modal('show');
        },
        error: function() {
            $('#postContent').html('<p>Error loading post details.</p>');
            $('#postModal').modal('show');
        }
    });
}
