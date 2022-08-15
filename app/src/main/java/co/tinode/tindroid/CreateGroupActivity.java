package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.tinode.tindroid.adapter.BaseRecyclerAdapter;
import co.tinode.tindroid.adapter.RecyclerViewHolder;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.NotConnectedException;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.model.ServerMessage;

public class CreateGroupActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<SelectWrapper<ComTopic<VxCard>>> adapter;
    private List<ComTopic<VxCard>> topics = new ArrayList<>();
    private final List<SelectWrapper<ComTopic<VxCard>>> data = new ArrayList<>();
    private Button confirmBtn;
    private TextInputEditText groupNameEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recycler_view);
        confirmBtn = findViewById(R.id.confirm_button);
        groupNameEt = findViewById(R.id.group_name_et);

//        newTopics.addAll(Cache.getTinode().getFilteredTopics(t ->
//                t.getTopicType().match(ComTopic.TopicType.USER) && t.getPub() != null));
        List<ComTopic<VxCard>> topics = new ArrayList<>(Cache.getTinode().getFilteredTopics(t ->
                t.getTopicType().match(ComTopic.TopicType.P2P) && t.getPub() != null));
        for (ComTopic<VxCard> topic : topics) {
            data.add(new SelectWrapper<>(topic, false));
        }
        adapter = new BaseRecyclerAdapter<SelectWrapper<ComTopic<VxCard>>>(this, data) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_contact_select;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, SelectWrapper<ComTopic<VxCard>> item) {
                ComTopic<VxCard> topic = item.getData();
                holder.setText(android.R.id.text1, topic.getPub().fn);
                holder.setText(android.R.id.text2, topic.getName());
                holder.getCheckBox(R.id.checkbox).setChecked(item.isSelected());
                ShapeableImageView avatarIv = (ShapeableImageView) holder.getView(R.id.avatar);
                avatarIv.setImageDrawable(
                        UiUtils.avatarDrawable(avatarIv.getContext(), null, topic.getPub().fn, topic.getName(), false));
            }

        };
        adapter.setOnItemClickListener((itemView, pos) -> {
            SelectWrapper<ComTopic<VxCard>> item = data.get(pos);
            item.setSelected(!item.isSelected());
            adapter.notifyItemChanged(pos);
            updateConfirmButtonEnableState();
        });
        recyclerView.setAdapter(adapter);

        groupNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateConfirmButtonEnableState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmBtn.setOnClickListener(v -> {
            String topicTitle = groupNameEt.getText().toString();

            String[] members = null;
            List<String> selectMemberList = new ArrayList<>();
            for (SelectWrapper<ComTopic<VxCard>> selectWrapper : data) {
                if (selectWrapper.isSelected()) {
                    selectMemberList.add(selectWrapper.getData().getName());
                }
            }
            members = selectMemberList.toArray(new String[]{});

            createTopic(this, topicTitle, null, "", false, null, members);
        });
    }

    private void createTopic(final Activity activity, final String title, final Bitmap avatar, final String subtitle,
                             final boolean isChannel, final String[] tags, final String[] members) {
        final ComTopic<VxCard> topic = new ComTopic<>(Cache.getTinode(), null, isChannel);
        topic.setComment(subtitle);
        topic.setTags(tags);
        topic.setPub(new VxCard(title));
        topic.subscribe().thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
            @Override
            public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                for (String user : members) {
                    topic.invite(user, null /* use default */);
                }

                Intent intent = new Intent(activity, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("topic", topic.getName());
                startActivity(intent);
                finish();
                return null;
            }
        }, new PromisedReply.FailureListener<ServerMessage>() {
            @Override
            public <E extends Exception> PromisedReply<ServerMessage> onFailure(E err) {
                runOnUiThread(() -> {
                    if (err instanceof NotConnectedException) {
                        Toast.makeText(activity, R.string.no_connection, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, R.string.action_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        });
    }

    private void updateConfirmButtonEnableState() {
        int selectCnt = 0;
        for (SelectWrapper<ComTopic<VxCard>> selectWrapper : data) {
            if (selectWrapper.isSelected()) {
                selectCnt++;
            }
        }
        if (selectCnt == 0) {
            confirmBtn.setEnabled(false);
            confirmBtn.setText(getResources().getString(R.string.confirm));
        } else {
            confirmBtn.setText(getResources().getString(R.string.confirm) + "(" + selectCnt + ")");
            if (TextUtils.isEmpty(groupNameEt.getText())) {
                confirmBtn.setEnabled(false);
            } else {
                confirmBtn.setEnabled(true);
            }
        }
    }

    @Override
    int getLayoutRes() {
        return R.layout.activity_create_group;
    }

    @Override
    String getTitleString() {
        return getResources().getString(R.string.create_group);
    }
}
