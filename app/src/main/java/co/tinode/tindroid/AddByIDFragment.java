package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.model.MetaGetDesc;
import co.tinode.tinodesdk.model.MsgGetMeta;
import co.tinode.tinodesdk.model.ServerMessage;

public class AddByIDFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_by_id, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        view.findViewById(R.id.confirm).setOnClickListener(view1 -> {
            TextView editor = activity.findViewById(R.id.editId);
            if (editor != null) {
                String id = editor.getText().toString();
                if (TextUtils.isEmpty(id)) {
                    editor.setError(getString(R.string.id_required));
                } else {
                    // 已经加入的群或者已经添加的好友，就直接进入
                    if (Cache.getTinode().getTopic(id) != null) {
                        gotoDetails(activity, id);
                        return;
                    }
                    ComTopic topic = (ComTopic<VxCard>) Cache.getTinode().newComTopic(id);
                    topic.setTinode(Cache.getTinode());
                    topic.getMeta(new MsgGetMeta(new MetaGetDesc(), null, null, null, null, false))
                            .thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                                @Override
                                public PromisedReply onSuccess(ServerMessage serverMessage) {
                                    VxCard pub = (VxCard) serverMessage.meta.desc.pub;
                                    if (pub.inviteOnlyOwner) {
                                        toastError(getActivity());
                                        return null;
                                    }
                                    gotoDetails(activity, id);
                                    return null;
                                }
                            })
                            .thenCatch(new UiUtils.ToastFailureListener(getActivity()));
                }
            }
        });
    }

    private void toastError(Activity activity) {
        if (activity != null) {
            activity.runOnUiThread(() -> {
                UiUtils.dismissKeyboard(activity);
                Toast.makeText(activity, R.string.block_join_group, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void gotoDetails(Activity activity, String id) {
        if (activity != null) {
            Intent it = new Intent(activity, MessageActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            it.putExtra("topic", id);
            startActivity(it);
            activity.finish();
        }
    }
}
