package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        loadUserStats();

        if (auth.getCurrentUser() != null) {
            binding.tvProfileEmail.setText(auth.getCurrentUser().getEmail());
        }

        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserStats() {
        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (value == null) {
                        return;
                    }

                    int totalTasks = value.size();
                    int completedTasks = 0;

                    for (var document : value.getDocuments()) {
                        Boolean completed = document.getBoolean("completed");
                        if (completed != null && completed) {
                            completedTasks++;
                        }
                    }

                    binding.tvTotalTasks.setText(getString(R.string.total_tasks_format, totalTasks));
                    binding.tvCompletedTasks.setText(getString(R.string.completed_tasks_format, completedTasks));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}