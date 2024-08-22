package com.friendbook.dto;

public class LikeResponse {
        private int likeCount;

        public LikeResponse(int likeCount) {
            this.likeCount = likeCount;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }
    }