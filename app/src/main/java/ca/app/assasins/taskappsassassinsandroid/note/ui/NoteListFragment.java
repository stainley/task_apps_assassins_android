package ca.app.assasins.taskappsassassinsandroid.note.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentNoteListBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NoteRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;

public class NoteListFragment extends Fragment {

    private FragmentNoteListBinding binding;

    private NoteViewModel noteViewModel;

    private CategoryViewModel categoryViewModel;

    private long categoryId;
    private List<Note> notesFiltered = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();

    private NoteRecycleAdapter noteRecycleAdapter;
    ArrayAdapter<String> adapterItems;
    String[] categories = new String[1000];
    String moveToCategories;
    int categoryCount = -1;
    AutoCompleteTextView autoCompleteTextView;

    boolean titleSortedByAsc = false;
    boolean createdDateSortedByAsc = false;


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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoteListBinding.inflate(inflater, container, false);

        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);
        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(requireActivity().getApplication())).get(NoteViewModel.class);

        RecyclerView noteListRecycleView = binding.noteList;
        noteRecycleAdapter = new NoteRecycleAdapter(notes, getOnCallbackAdapter(notes));
        noteListRecycleView.setAdapter(noteRecycleAdapter);
        noteListRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        SearchView searchView = binding.searchView;
        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());
        searchView.inflateMenu(R.menu.search_bar_menu);
        // TODO: add implementation for tap mic to search
        searchView.setOnMenuItemClickListener(item -> {

            Toast.makeText(getContext(), "Add implementation for voice for search", Toast.LENGTH_SHORT).show();
            return true;
        });

        binding.createNoteBtn.setOnClickListener(this::createNewNote);
        binding.sortButton.setOnClickListener(this::sortButtonClicked);

        MaterialButton noteWithAudioBtn = binding.noteWithAudioBtn;
        MaterialButton noteWithImagesBtn = binding.noteWithImagesBtn;
        noteWithAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setHint("Note with audio");
                // TODO: search on the DB with audio
                RecyclerView noteFilterRecycle = binding.noteFilterRecycle;
                noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));

                List<Note> noteWithAudio = new ArrayList<>();
                noteRecycleAdapter = new NoteRecycleAdapter(noteWithAudio, getOnCallbackAdapter(noteWithAudio));
                noteViewModel.fetchAllNotesWithAudio(categoryId).observe(getViewLifecycleOwner(), noteAudios -> noteAudios.forEach(n -> {
                    if (n.getAudios().size() > 0)
                        noteWithAudio.add(n.getNote());
                }));
                noteFilterRecycle.setAdapter(noteRecycleAdapter);
                noteRecycleAdapter.notifyDataSetChanged();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences categorySP = requireActivity().getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        categoryCount = categorySP.getInt("categoryCount", -1);
        moveToCategories = categorySP.getString("moveToCategories", "");
        categories = moveToCategories.split(",");

        binding.titleCategory.setText(categorySP.getString("categoryName", ""));

        noteViewModel.fetchAllNoteByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
            this.notes.clear();
            this.notes.addAll(notesResult);

            this.noteRecycleAdapter.notifyItemChanged(notesResult.size());
        });

        Toolbar noteAppBar = binding.noteAppBar;
        requireActivity().setActionBar(noteAppBar);

        ActionBar actionBar = requireActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        noteAppBar.setNavigationOnClickListener(v -> requireActivity().finish());
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

    private void sortButtonClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = (View) inflater.inflate(R.layout.activity_sort_sheet, null);
        bottomSheetView = bottomSheetView.findViewById(R.id.bottomSheetSortContainer);

        bottomSheetView.findViewById(R.id.sort_by_title).setOnClickListener(view1 -> {

            if (titleSortedByAsc) {
                titleSortedByAsc = false;
                noteViewModel.fetchAllDescByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            } else {
                titleSortedByAsc = true;
                noteViewModel.fetchAllAscByCategory(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            if (createdDateSortedByAsc) {
                createdDateSortedByAsc = false;
                noteViewModel.fetchAllNotesOrderByDateDesc(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            } else {
                createdDateSortedByAsc = true;
                noteViewModel.fetchAllNotesOrderByDateAsc(categoryId).observe(getViewLifecycleOwner(), notesResult -> {
                    refreshNotes(notesResult);
                });
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void refreshNotes(List<Note> result) {
        this.notes.clear();
        this.notes.addAll(result);

        this.noteRecycleAdapter.notifyItemChanged(result.size());
        this.noteRecycleAdapter.notifyDataSetChanged();
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
                noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));

                // filter category that contain x value
                notesFiltered = notes.stream().filter(note -> {
                    if (s.length() == 0) return false;
                    return note.getTitle().toLowerCase().contains(s.toString().toLowerCase()) || note.getDescription().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                noteRecycleAdapter = new NoteRecycleAdapter(notesFiltered, getOnCallbackAdapter(notesFiltered));
                noteFilterRecycle.setAdapter(noteRecycleAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @NonNull
    private NoteRecycleAdapter.OnNoteCallback getOnCallbackAdapter(List<Note> notes) {
        return new NoteRecycleAdapter.OnNoteCallback() {
            @Override
            public void onNoteSelected(View view, int position) {
                Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity().setOldNote(notes.get(position)));
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
            public void onMoveNote(int position, Note note) {
                LayoutInflater inflater = getLayoutInflater();
                View moveNoteView = (View) inflater.inflate(R.layout.categories_dropdown, null);
                autoCompleteTextView = moveNoteView.findViewById(R.id.auto_complete_txt);

                adapterItems = new ArrayAdapter<>(getContext(), R.layout.categories_dropdown_items, categories);
                autoCompleteTextView.setAdapter(adapterItems);
                autoCompleteTextView.setOnItemClickListener((adapterView, view, position1, id) -> {
                    String category = adapterView.getItemAtPosition(position1).toString();

                    categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result -> {
                        note.setCategoryId(result.getId());
                        noteViewModel.updateNote(note);
                    });
                });

                new MaterialAlertDialogBuilder(requireActivity()).setTitle("Move Note").setView(moveNoteView).setNeutralButton("Cancel", (dialog, which) -> {

                }).setNegativeButton("Done", (dialog, which) -> {

                }).setCancelable(false).show();
            }

            @Override
            public void onDisplayThumbnail(ImageView Imageview, int position) {
                noteViewModel.findPictureByNoteId(notes.get(position).getNoteId()).observe(getViewLifecycleOwner(), noteImages -> {
                    if (noteImages != null && noteImages.getPictures().size() > 0) {
                        String path = noteImages.getPictures().get(0).getPath();
                        Picasso.get().load(path).resize(200, 200).centerInside().into(Imageview);
                    }
                });
            }

            @Override
            public void showAudioIcon(ImageButton audioIcon, int position) {
                noteViewModel.fetchAudiosByNote(notes.get(position).getNoteId()).observe(getViewLifecycleOwner(), noteAudios -> noteAudios.forEach(noteAudios1 -> {
                    if (noteAudios1.getAudios().size() > 0) {
                        audioIcon.setVisibility(View.VISIBLE);
                    }
                }));
            }
        };
    }
}
