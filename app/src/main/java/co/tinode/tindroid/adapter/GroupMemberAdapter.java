package co.tinode.tindroid.adapter;

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
    }
}

