package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentAddTaskBinding;
import com.example.myapplication.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = binding.etTaskTitle.getText().toString().trim();
        String description = binding.etTaskDescription.getText().toString().trim();
        String priority = getSelectedPriority();

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), R.string.enter_task_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Task task = new Task(
                userId,
                title,
                description,
                priority,
                false,
                System.currentTimeMillis()
        );

        db.collection("tasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), R.string.task_saved, Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String getSelectedPriority() {
        int selectedId = binding.rgPriority.getCheckedRadioButtonId();

        if (selectedId == binding.rbHigh.getId()) {
            return "High";
        }

        if (selectedId == binding.rbLow.getId()) {
            return "Low";
        }

        return "Medium";
    }

    private void clearFields() {
        binding.etTaskTitle.setText("");
        binding.etTaskDescription.setText("");
        binding.rbMedium.setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}