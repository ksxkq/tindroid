package co.tinode.tindroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.tinode.tindroid.adapter.BaseRecyclerAdapter;
import co.tinode.tindroid.adapter.RecyclerViewHolder;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.Topic;

public class CreateGroupActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<ComTopic<VxCard>> adapter;
    private List<ComTopic<VxCard>> newTopics = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recycler_view);

//        newTopics.addAll(Cache.getTinode().getFilteredTopics(t ->
//                t.getTopicType().match(ComTopic.TopicType.USER) && t.getPub() != null));
        newTopics.addAll(Cache.getTinode().getFilteredTopics(t ->
                t.getTopicType().match(ComTopic.TopicType.P2P) && t.getPub() != null));
        adapter = new BaseRecyclerAdapter<ComTopic<VxCard>>(this, newTopics) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.contact_basic;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, ComTopic<VxCard> item) {
                holder.setText(android.R.id.text1, item.getPub().fn);
                holder.setText(android.R.id.text2, item.getName());
            }
        };
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
