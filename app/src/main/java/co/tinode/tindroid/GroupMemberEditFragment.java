package co.tinode.tindroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.tinode.tindroid.adapter.BaseRecyclerAdapter;
import co.tinode.tindroid.adapter.GroupMemberAdapter;
import co.tinode.tindroid.adapter.RecyclerViewHolder;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.Subscription;

public class GroupMemberEditFragment extends BaseFragment {

    private ComTopic<VxCard> mTopic;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<SelectWrapper<GroupMemberAdapter.GroupMember>> adapter;
    private final List<SelectWrapper<GroupMemberAdapter.GroupMember>> data = new ArrayList<>();
    private Button confirmBtn;
    private boolean isDataChanged;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        confirmBtn = view.findViewById(R.id.confirm_button);
        Bundle args = getArguments();
        String topicName = args.getString(AttachmentHandler.ARG_TOPIC_NAME);
        final Tinode tinode = Cache.getTinode();
        mTopic = (ComTopic<VxCard>) tinode.getTopic(topicName);

        List<ComTopic<VxCard>> topics = new ArrayList<>(Cache.getTinode().getFilteredTopics(t ->
                t.getTopicType().match(ComTopic.TopicType.P2P) && t.getPub() != null));
        for (ComTopic<VxCard> topic : topics) {
            GroupMemberAdapter.GroupMember groupMember = new GroupMemberAdapter.GroupMember(topic.getPub().fn, topic.getName());
            data.add(new SelectWrapper<>(groupMember, false));
        }

        Collection<Subscription<VxCard, PrivateType>> subs = mTopic.getSubscriptions();
        if (subs != null) {
            boolean isManager = mTopic.isManager();
            for (Subscription<VxCard, PrivateType> sub : subs) {
                for (SelectWrapper<GroupMemberAdapter.GroupMember> memberSelectWrapper : data) {
                    if (memberSelectWrapper.getData().getUser().equals(sub.user)) {
                        memberSelectWrapper.setSelected(true);
                        break;
                    }
                }
            }
        }

        adapter = new BaseRecyclerAdapter<SelectWrapper<GroupMemberAdapter.GroupMember>>(getActivity(), data) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_contact_select;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, SelectWrapper<GroupMemberAdapter.GroupMember> item) {
                GroupMemberAdapter.GroupMember member = item.getData();
                String nickName = member.getNickName();
                holder.setText(android.R.id.text1, nickName);
                String user = member.getUser();
                holder.setText(android.R.id.text2, user);
                holder.getCheckBox(R.id.checkbox).setChecked(item.isSelected());
                ShapeableImageView avatarIv = (ShapeableImageView) holder.getView(R.id.avatar);
                avatarIv.setImageDrawable(
                        UiUtils.avatarDrawable(avatarIv.getContext(), null, nickName, user, false));
            }

        };
        adapter.setOnItemClickListener((itemView, pos) -> {
            SelectWrapper<GroupMemberAdapter.GroupMember> item = data.get(pos);
            item.setSelected(!item.isSelected());
            adapter.notifyItemChanged(pos);
            isDataChanged = true;
            updateConfirmButtonEnableState();
        });
        recyclerView.setAdapter(adapter);
        updateConfirmButtonEnableState();
    }

    private void updateConfirmButtonEnableState() {
        int selectCnt = 0;
        for (SelectWrapper<GroupMemberAdapter.GroupMember> selectWrapper : data) {
            if (selectWrapper.isSelected()) {
                selectCnt++;
            }
        }
        if (!isDataChanged) {
            confirmBtn.setEnabled(false);
            confirmBtn.setText(getResources().getString(R.string.confirm));
        } else {
            confirmBtn.setEnabled(true);
            confirmBtn.setText(getResources().getString(R.string.confirm) + "(" + selectCnt + ")");
        }
    }

    @Override
    int getLayout() {
        return R.layout.fragment_edit_group_members;
    }
}
