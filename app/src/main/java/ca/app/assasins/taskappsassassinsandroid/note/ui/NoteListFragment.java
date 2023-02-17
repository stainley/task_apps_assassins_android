package ca.app.assasins.taskappsassassinsandroid.note.ui;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Comparator.comparing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.model.Category;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentNoteListBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.ui.adpter.NoteRecycleAdapter;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModel;
import ca.app.assasins.taskappsassassinsandroid.note.viewmodel.NoteViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.task.model.SubTask;

public class NoteListFragment extends Fragment implements NoteRecycleAdapter.OnNoteCallback {

    private FragmentNoteListBinding binding;

    private NoteViewModel noteViewModel;

    private CategoryViewModel categoryViewModel;
    private Category category;

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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoteListBinding.inflate(inflater, container, false);

        SharedPreferences categorySP = requireActivity().getSharedPreferences("category_sp", MODE_PRIVATE);
        categoryId = categorySP.getLong("categoryId", -1);
        categoryCount = categorySP.getInt("categoryCount", -1);
        moveToCategories = categorySP.getString("moveToCategories", "");
        categories = moveToCategories.split(",");

        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);
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
        binding.sortButton.setOnClickListener(this::sortButtonClicked);

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

    private void sortButtonClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = (View) inflater.inflate(R.layout.activity_sort_sheet, null);
        bottomSheetView = bottomSheetView.findViewById(R.id.bottomSheetSortContainer);

        bottomSheetView.findViewById(R.id.sort_by_title).setOnClickListener(view1 -> {
            titleSortedByAsc = !titleSortedByAsc;

            if (titleSortedByAsc) {
                notes.sort(comparing(Note::getTitle));
            }
            else {
                notes.sort(comparing(Note::getTitle).reversed());
            }
            noteRecycleAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            createdDateSortedByAsc = !createdDateSortedByAsc;

            if (createdDateSortedByAsc) {
                notes.sort(comparing(Note::getCreatedDate));
            }
            else {
                notes.sort(comparing(Note::getCreatedDate).reversed());
            }
            noteRecycleAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onNoteSelected(View view, int position) {
        Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity().setOldNote(notes.get(position)));
    }

    @Override
    public void onMoveNote(int position, Note note) {
        LayoutInflater inflater = getLayoutInflater();
        View moveNoteView = (View) inflater.inflate(R.layout.categories_dropdown, null);
        autoCompleteTextView = moveNoteView.findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(getContext(), R.layout.categories_dropdown_items, categories);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, position1, id) -> {
            String category = adapterView.getItemAtPosition(position1).toString();

            categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result -> {
                System.out.println("note " + note.getTitle());
                note.setCategoryId(result.getId());
                noteViewModel.updateNote(note);
            });
        });

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Move Note")
                .setView(moveNoteView)
                .setNeutralButton("Cancel", (dialog, which) -> {

                }).setNegativeButton("Done", (dialog, which) -> {

                }).setCancelable(false).show();
    }

    @Override
    public void onDisplayThumbnail(ImageView view, int position) {

        noteViewModel.findPictureByNoteId(notes.get(position).getNoteId()).observe(this, noteImages -> {
            if (noteImages != null && noteImages.getPictures().size() > 0) {
                String path = noteImages.getPictures().get(0).getPath();
                Picasso.get().load(path)
                        .resize(200, 200)
                        .centerInside()
                        .into(view);
            }
        });

    }

    @Override
    public void showAudioIcon(ImageButton audioIcon, int position) {

        noteViewModel.fetchAudiosByNote(notes.get(position).getNoteId()).observe(this, noteAudios -> noteAudios.forEach(noteAudios1 -> {
            if (noteAudios1.getAudios().size() > 0) {
                audioIcon.setVisibility(View.VISIBLE);
            }
        }));

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

                noteRecycleAdapter = new NoteRecycleAdapter(notesFiltered, NoteListFragment.this);

                noteFilterRecycle.setAdapter(noteRecycleAdapter);
                noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
