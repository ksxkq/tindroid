package co.tinode.tindroid;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.ComTopic;

public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ComTopic<VxCard>> newTopics = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        update();

        recyclerView = view.findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerView.Adapter<ContactViewHolder>() {
            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(viewType, parent, false);
                return new ContactViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
                ComTopic<VxCard> vxCardComTopic = newTopics.get(position);
                String topicName = vxCardComTopic.getName();
                holder.contactPriv.setText(topicName);
                VxCard pub = vxCardComTopic.getPub();
                if (pub != null && pub.fn != null) {
                    holder.name.setText(pub.fn);
                    holder.name.setTypeface(null, Typeface.NORMAL);
                } else {
                    holder.name.setText(R.string.placeholder_contact_title);
                    holder.name.setTypeface(null, Typeface.ITALIC);
                }
                UiUtils.setAvatar(holder.avatar, pub, topicName, vxCardComTopic.isDeleted());
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), MessageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("topic", topicName);
                    getActivity().startActivity(intent);
                });
            }

            @Override
            public int getItemViewType(int position) {
                return R.layout.item_contact;
            }

            @Override
            public int getItemCount() {
                return newTopics.size();
            }
        });
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView contactPriv;
        public ShapeableImageView avatar;

        public ContactViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            contactPriv = itemView.findViewById(R.id.contactPriv);
            avatar = itemView.findViewById(R.id.avatar);
        }


    }

    private void update() {
        newTopics.clear();
        newTopics.addAll(Cache.getTinode().getFilteredTopics(t ->
                t.getTopicType().match(ComTopic.TopicType.USER) && t.getPub() != null));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_chats, menu); // keep same from chatfragment
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(getActivity(), FindByIDActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
