package co.tinode.tindroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import co.tinode.tindroid.adapter.BaseRecyclerAdapter;
import co.tinode.tindroid.adapter.RecyclerViewHolder;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;

public class CreateGroupActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<SelectWrapper<ComTopic<VxCard>>> adapter;
    private List<ComTopic<VxCard>> topics = new ArrayList<>();
    private List<SelectWrapper<ComTopic<VxCard>>> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recycler_view);

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
        });
        recyclerView.setAdapter(adapter);
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
