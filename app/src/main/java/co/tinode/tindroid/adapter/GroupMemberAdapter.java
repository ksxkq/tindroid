package co.tinode.tindroid.adapter;

import java.util.Objects;

public class GroupMemberAdapter {

    public static class GroupMember {
        private String nickName;
        private String user;

        public GroupMember(String nickName, String user) {
            this.nickName = nickName;
            this.user = user;
        }

        public String getNickName() {
            return nickName;
        }

        public String getUser() {
            return user;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupMember that = (GroupMember) o;
            return user.equals(that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user);
        }
    }
}

