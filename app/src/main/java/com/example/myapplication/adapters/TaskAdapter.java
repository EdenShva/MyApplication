package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemTaskBinding;
import com.example.myapplication.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onTaskChecked(Task task, boolean isChecked);
        void onTaskDeleted(Task task);
    }

    private final List<Task> tasks;
    private final TaskActionListener listener;

    public TaskAdapter(List<Task> tasks, TaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Task task, TaskActionListener listener) {
            binding.cbCompleted.setOnCheckedChangeListener(null);

            binding.cbCompleted.setText(task.getTitle());
            binding.cbCompleted.setChecked(task.isCompleted());
            binding.tvTaskDescription.setText(task.getDescription());
            binding.tvTaskPriority.setText(task.getPriority());

            binding.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) ->
                    listener.onTaskChecked(task, isChecked)
            );

            binding.btnDeleteTask.setOnClickListener(v ->
                    listener.onTaskDeleted(task)
            );
        }
    }
}