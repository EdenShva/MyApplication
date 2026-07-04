package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapters.TaskAdapter;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TaskAdapter adapter;
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        adapter = new TaskAdapter(tasks, new TaskAdapter.TaskActionListener() {
            @Override
            public void onTaskChecked(Task task, boolean isChecked) {
                updateTaskStatus(task, isChecked);
            }

            @Override
            public void onTaskDeleted(Task task) {
                deleteTask(task);
            }
        });
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(adapter);

        loadTasks();
    }

    private void loadTasks() {
        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tasks.clear();

                    if (value != null) {
                        for (var document : value.getDocuments()) {
                            Task task = document.toObject(Task.class);
                            if (task != null) {
                                task.setId(document.getId());
                                tasks.add(task);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    binding.tvEmptyTasks.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
    private void updateTaskStatus(Task task, boolean completed) {
        db.collection("tasks")
                .document(task.getId())
                .update("completed", completed);
    }

    private void deleteTask(Task task) {
        db.collection("tasks")
                .document(task.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}