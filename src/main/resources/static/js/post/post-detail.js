const likeBtn = document.getElementById('likeBtn');
likeBtn.addEventListener('click', function() {
    const postId = this.getAttribute('data-post-id');

    // AJAX 호출
    fetch(`/posts/${postId}/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        // body: 필요하다면 JSON.stringify(...) 추가 가능
    })
        .then(response => response.json())
        .then(data => {
            // data = { liked: true/false, likeCount: 10 }

            // 버튼 모양/텍스트 변경
            likeBtn.setAttribute('data-liked', data.liked);

            if (data.liked) {
                // 좋아요가 새로 눌렸음
                likeBtn.textContent = `좋아요 (${data.likeCount})`;
            } else {
                // 좋아요 취소됨
                likeBtn.textContent = `좋아요 취소 (${data.likeCount})`;
            }
        })
        .catch(error => console.error(error));
});