package com.example.roomdatabasemvvm.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabasemvvm.Note;
import com.example.roomdatabasemvvm.R;
import com.example.roomdatabasemvvm.databinding.NoteItemBinding;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final Context context;
    private List<Note> noteList;
    private OnNoteItemClickListener listener;
    private static final int START_POSITION = 0;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        NoteItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.note_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.binding.setNoteModel(note);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
//        notifyItemInserted(START_POSITION);
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return noteList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final NoteItemBinding binding;

        public ViewHolder(@NonNull NoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onNoteClicked(binding.getNoteModel());
                    }
                }
            });
        }
    }

    public interface OnNoteItemClickListener {
        void onNoteClicked(Note note);
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener) {
        this.listener = listener;
    }
}
