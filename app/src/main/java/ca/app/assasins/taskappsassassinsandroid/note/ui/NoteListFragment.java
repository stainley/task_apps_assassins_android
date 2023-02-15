package ca.app.assasins.taskappsassassinsandroid.note.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentNoteListBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NoteRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;

public class NoteListFragment extends Fragment implements NoteRecycleAdapter.OnNoteCallback {

    private FragmentNoteListBinding binding;

    private NoteViewModel noteViewModel;
    private Category category;

    private long categoryId;

    private List<Note> notesFiltered = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();

    private NoteRecycleAdapter noteRecycleAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoteListBinding.inflate(inflater, container, false);

        SharedPreferences categorySP = requireActivity().getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);


        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(requireActivity().getApplication())).get(NoteViewModel.class);

        noteViewModel.fetchAllNoteByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
            this.notes.clear();
            this.notes.addAll(notesResult);
            this.noteRecycleAdapter.notifyItemChanged(notesResult.size());
        });

        RecyclerView noteListRecycleView = binding.noteList;
        noteRecycleAdapter = new NoteRecycleAdapter(notes, this);
        noteListRecycleView.setAdapter(noteRecycleAdapter);
        noteListRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        SearchView searchView = binding.searchView;

        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        binding.createNoteBtn.setOnClickListener(this::createNewNote);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        assert arguments != null;
        category = (Category) arguments.getSerializable("category");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        noteRecycleAdapter.notifyDataSetChanged();
    }

    private void createNewNote(View view) {
        Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity());
    }

    @Override
    public void onNoteSelected(View view, int position) {
        Toast.makeText(getContext(), "CLICKED RECORD: " + notes.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity().setOldNote(notes.get(position)));
    }

    @Override
    public void onMoveNote(int position) {

    }

    @Override
    public void onDeleteNote(View view, int position) {
        Snackbar deleteSnackbar = Snackbar.make(view, "Are sure you want do delete this note?", Snackbar.LENGTH_LONG).setAnchorView(binding.createNoteBtn).setAction("Accept", v -> {
            noteViewModel.deleteNote(notes.get(position));
            notes.remove(notes.get(position));
            noteRecycleAdapter.notifyItemRemoved(position);
        });
        deleteSnackbar.show();
    }

    private Supplier<TextWatcher> getTextWatcherSupplier() {
        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesFiltered.clear();
                RecyclerView noteFilterRecycle = binding.noteFilterRecycle;

                // filter category that contain x value
                notesFiltered = notes.stream().filter(note -> {

                    if (s.length() == 0) return false;
                    return note.getTitle().toLowerCase().contains(s.toString().toLowerCase()) ||
                            note.getDescription().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                noteRecycleAdapter = new NoteRecycleAdapter(notesFiltered, new NoteRecycleAdapter.OnNoteCallback() {
                    @Override
                    public void onMoveNote(int position) {

                    }

                    @Override
                    public void onDeleteNote(View view, int position) {
                        Snackbar deleteSnackbar = Snackbar.make(view, "Are sure you want do delete this note?", Snackbar.LENGTH_LONG).setAnchorView(binding.createNoteBtn).setAction("Accept", v -> {
                            noteViewModel.deleteNote(notes.get(position));
                            notes.remove(notes.get(position));
                            noteRecycleAdapter.notifyItemRemoved(position);
                        });
                        deleteSnackbar.show();
                    }

                    @Override
                    public void onNoteSelected(View view, int position) {
                        Toast.makeText(getContext(), "CLICKED RECORD: " + notes.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity().setOldNote(notes.get(position)));
                    }
                });

                noteFilterRecycle.setAdapter(noteRecycleAdapter);
                noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}