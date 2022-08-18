package co.tinode.tindroid;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.MetaGetDesc;
import co.tinode.tinodesdk.model.MsgGetMeta;
import co.tinode.tinodesdk.model.MsgSetMeta;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.ServerMessage;

public class GroupSettingFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        String topicName = args.getString(AttachmentHandler.ARG_TOPIC_NAME);
        final Tinode tinode = Cache.getTinode();
        ComTopic<VxCard> topic = (ComTopic<VxCard>) tinode.getTopic(topicName);

        View onlyOwnerInviteLl = view.findViewById(R.id.only_owner_invite_ll);
        SwitchCompat onlyOwnerInviteSw = view.findViewById(R.id.only_owner_invite_sw);
        View groupMuteLl = view.findViewById(R.id.group_mute_ll);
        SwitchCompat groupMuteSw = view.findViewById(R.id.group_mute_sw);
        View blockP2pLl = view.findViewById(R.id.block_p2p_ll);
        SwitchCompat blockP2pSw = view.findViewById(R.id.block_p2p_sw);

        onlyOwnerInviteSw.setChecked(topic.getPub().inviteOnlyOwner);

        onlyOwnerInviteLl.setOnClickListener(v -> {
            VxCard pub = topic.getPub().copy();
            pub.inviteOnlyOwner = !onlyOwnerInviteSw.isChecked();
            topic.setDescription(pub, null, null).thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                @Override
                public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                    topic.setPub(pub);
//                    topic.getMeta(new MsgGetMeta(new MetaGetDesc(), null, null, null, null, false)).thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
//                        @Override
//                        public PromisedReply<ServerMessage> onSuccess(ServerMessage result) throws Exception {
//                            return null;
//                        }
//                    });
                    onMainThread(() -> onlyOwnerInviteSw.setChecked(pub.inviteOnlyOwner));
                    return null;
                }
            }).thenCatch(new UiUtils.ToastFailureListener(getActivity()));
        });
    }

    private void onMainThread(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }

    @Override
    int getLayout() {
        return R.layout.fragment_group_setting;
    }
}
