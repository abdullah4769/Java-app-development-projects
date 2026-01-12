package com.example.aibasedcareercounsellingapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aibasedcareercounsellingapp.R;
import com.example.aibasedcareercounsellingapp.databinding.ItemSkillBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for displaying skills in RecyclerView with multi-select capability
 */
public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> implements Filterable {
    
    private List<String> allSkills;
    private List<String> filteredSkills;
    private Set<String> selectedSkills;
    private OnSelectionChangedListener listener;
    
    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }
    
    public SkillAdapter(List<String> skills, OnSelectionChangedListener listener) {
        this.allSkills = new ArrayList<>(skills);
        this.filteredSkills = new ArrayList<>(skills);
        this.selectedSkills = new HashSet<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSkillBinding binding = ItemSkillBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SkillViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        String skill = filteredSkills.get(position);
        holder.bind(skill, selectedSkills.contains(skill));
    }
    
    @Override
    public int getItemCount() {
        return filteredSkills.size();
    }
    
    public Set<String> getSelectedSkills() {
        return new HashSet<>(selectedSkills);
    }
    
    public int getSelectedCount() {
        return selectedSkills.size();
    }
    
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filtered = new ArrayList<>();
                
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(allSkills);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String skill : allSkills) {
                        if (skill.toLowerCase().contains(filterPattern)) {
                            filtered.add(skill);
                        }
                    }
                }
                
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }
            
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredSkills.clear();
                filteredSkills.addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };
    }
    
    class SkillViewHolder extends RecyclerView.ViewHolder {
        private ItemSkillBinding binding;
        
        public SkillViewHolder(ItemSkillBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(String skill, boolean isSelected) {
            binding.skillName.setText(skill);
            
            // Update UI based on selection state
            if (isSelected) {
                binding.chipContainer.setBackgroundResource(R.drawable.bg_skill_chip_selected);
                binding.skillName.setTextColor(Color.WHITE);
                binding.checkIcon.setVisibility(View.VISIBLE);
            } else {
                binding.chipContainer.setBackgroundResource(R.drawable.bg_skill_chip_unselected);
                binding.skillName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
                binding.checkIcon.setVisibility(View.GONE);
            }
            
            // Handle click
            binding.skillCard.setOnClickListener(v -> {
                if (selectedSkills.contains(skill)) {
                    selectedSkills.remove(skill);
                } else {
                    selectedSkills.add(skill);
                }
                notifyItemChanged(getAdapterPosition());
                if (listener != null) {
                    listener.onSelectionChanged(selectedSkills.size());
                }
            });
        }
    }
}
