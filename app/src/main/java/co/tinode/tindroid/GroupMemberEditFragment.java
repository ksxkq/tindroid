package co.tinode.tindroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import co.tinode.tinodesdk.NotConnectedException;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.ServerMessage;
import co.tinode.tinodesdk.model.Subscription;

public class GroupMemberEditFragment extends BaseFragment {

    private ComTopic<VxCard> mTopic;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<SelectWrapper<GroupMemberAdapter.GroupMember>> adapter;
    private final List<SelectWrapper<GroupMemberAdapter.GroupMember>> data = new ArrayList<>();
    private Button confirmBtn;
    private List<GroupMemberAdapter.GroupMember> originalSelectedMemberList = new ArrayList<>();
    private List<GroupMemberAdapter.GroupMember> removedMemberList = new ArrayList<>();
    private List<GroupMemberAdapter.GroupMember> addMemberList = new ArrayList<>();
    private List<GroupMemberAdapter.GroupMember> currentSelectedMemberList = new ArrayList<>();

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
            for (Subscription<VxCard, PrivateType> sub : subs) {
                for (SelectWrapper<GroupMemberAdapter.GroupMember> memberSelectWrapper : data) {
                    if (memberSelectWrapper.getData().getUser().equals(sub.user)) {
                        memberSelectWrapper.setSelected(true);
                        originalSelectedMemberList.add(memberSelectWrapper.getData());
                        break;
                    }
                }
            }
        }
        currentSelectedMemberList.addAll(originalSelectedMemberList);

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
            boolean isSelect = !item.isSelected();
            if (isSelect) {
                currentSelectedMemberList.add(item.getData());
            } else {
                currentSelectedMemberList.remove(item.getData());
            }
            item.setSelected(isSelect);
            adapter.notifyItemChanged(pos);
            updateConfirmButtonEnableState();
        });
        recyclerView.setAdapter(adapter);
        updateConfirmButtonEnableState();

        confirmBtn.setOnClickListener(v -> updateContacts(getActivity()));
    }

    private void updateContacts(final Activity activity) {
        try {
            List<GroupMemberAdapter.GroupMember> removedMemberList = new ArrayList<>(originalSelectedMemberList);
            List<GroupMemberAdapter.GroupMember> addMemberList = new ArrayList<>(currentSelectedMemberList);
            removedMemberList.removeAll(currentSelectedMemberList);
            addMemberList.removeAll(originalSelectedMemberList);
            PromisedReply.FailureListener<ServerMessage> failureListener = new UiUtils.ToastFailureListener(getActivity());
            for (GroupMemberAdapter.GroupMember addMember : addMemberList) {
                mTopic.invite(addMember.getUser(), null /* use default */).thenCatch(failureListener);
            }
            for (GroupMemberAdapter.GroupMember removedMember : removedMemberList) {
                mTopic.eject(removedMember.getUser(), false).thenCatch(failureListener);
            }
            getActivity().finish();
        } catch (NotConnectedException ignored) {
            Toast.makeText(activity, R.string.no_connection, Toast.LENGTH_SHORT).show();
            // Go back to contacts
        } catch (Exception ex) {
            Toast.makeText(activity, R.string.action_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateConfirmButtonEnableState() {
        // 检测变化
        removedMemberList.clear();
        addMemberList.clear();
        List<GroupMemberAdapter.GroupMember> currentSelectedMemberList = new ArrayList<>();
        for (SelectWrapper<GroupMemberAdapter.GroupMember> memberSelectWrapper : data) {
            if (memberSelectWrapper.isSelected()) {
                currentSelectedMemberList.add(memberSelectWrapper.getData());
            }
        }
        List<GroupMemberAdapter.GroupMember> removedMemberList = new ArrayList<>(originalSelectedMemberList);
        List<GroupMemberAdapter.GroupMember> addMemberList = new ArrayList<>(currentSelectedMemberList);
        removedMemberList.removeAll(currentSelectedMemberList);
        addMemberList.removeAll(originalSelectedMemberList);
        if (removedMemberList.size() == 0 && addMemberList.size() == 0) {
            confirmBtn.setEnabled(false);
            confirmBtn.setText(getResources().getString(R.string.confirm));
        } else {
            confirmBtn.setEnabled(true);
            confirmBtn.setText(getResources().getString(R.string.confirm) + "(" + currentSelectedMemberList.size() + ")");
        }
    }

    @Override
    int getLayout() {
        return R.layout.fragment_edit_group_members;
    }
}
