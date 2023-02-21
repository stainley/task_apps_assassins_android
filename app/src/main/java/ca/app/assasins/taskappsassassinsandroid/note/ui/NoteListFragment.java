package ca.app.assasins.taskappsassassinsandroid.note.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.app.assasins.taskappsassassinsandroid.R;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModel;
import ca.app.assasins.taskappsassassinsandroid.category.viewmodel.CategoryViewModelFactory;
import ca.app.assasins.taskappsassassinsandroid.common.helper.MediaType;
import ca.app.assasins.taskappsassassinsandroid.databinding.FragmentNoteListBinding;
import ca.app.assasins.taskappsassassinsandroid.note.model.Color;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
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
    private NoteRecycleAdapter noteRecycleAdapterFiltered;
    ArrayAdapter<String> adapterItems;
    String[] categories = new String[1000];
    String moveToCategories;
    int categoryCount = -1;
    AutoCompleteTextView autoCompleteTextView;

    boolean titleSortedByAsc = false;
    boolean createdDateSortedByAsc = false;
    private SearchView searchView;

    private final ActivityResultLauncher<Intent> textToSpeakLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                List<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                searchView.getEditText().setText(spokenText);
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoteListBinding.inflate(inflater, container, false);

        categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);
        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(requireActivity().getApplication())).get(NoteViewModel.class);

        RecyclerView noteListRecycleView = binding.noteList;
        noteRecycleAdapter = new NoteRecycleAdapter(notes, getOnCallbackAdapter(notes));
        noteListRecycleView.setAdapter(noteRecycleAdapter);
        noteListRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        searchView = binding.searchView;
        searchView.getEditText().addTextChangedListener(getTextWatcherSupplier().get());

        searchView.inflateMenu(R.menu.search_bar_menu);
        searchView.setOnMenuItemClickListener(item -> {
            displaySpeechRecognizer();
            return true;
        });

        binding.createNoteBtn.setOnClickListener(this::createNewNote);
        binding.sortButton.setOnClickListener(this::sortButtonClicked);

        binding.noteWithAudioBtn.setOnClickListener(this::filterNoteWithAudio);
        binding.noteWithImagesBtn.setOnClickListener(this::filterNoteWithImage);

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

            this.noteRecycleAdapter.notifyDataSetChanged();
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

    private void filterNoteWithImage(View view) {

        searchView.setHint("Note with image");
        filterNotes(MediaType.PICTURE);

    }

    private void filterNoteWithAudio(View view) {
        searchView.setHint("Note with audio");
        filterNotes(MediaType.AUDIO);
    }

    private void filterNotes(MediaType mediaType) {
        RecyclerView noteFilterRecycle = binding.noteFilterRecycle;
        List<Note> noteFiltered = new ArrayList<>();
        noteRecycleAdapterFiltered = new NoteRecycleAdapter(noteFiltered, getOnCallbackAdapter(noteFiltered));
        noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));

        switch (mediaType) {
            case PICTURE:
                noteViewModel.fetchAllNotesWithImage(categoryId).observe(getViewLifecycleOwner(), noteImages -> noteImages.forEach(note -> {
                    if (note.getPictures().size() > 0) {
                        noteFiltered.add(note.getNote());
                        noteFilterRecycle.setAdapter(noteRecycleAdapterFiltered);
                        noteRecycleAdapterFiltered.notifyDataSetChanged();
                    }
                }));
                break;
            case AUDIO:
                noteViewModel.fetchAllNotesWithAudio(categoryId).observe(getViewLifecycleOwner(), noteAudios -> noteAudios.forEach(n -> {
                    if (n.getAudios().size() > 0) {
                        noteFiltered.add(n.getNote());
                        noteFilterRecycle.setAdapter(noteRecycleAdapterFiltered);
                        noteRecycleAdapterFiltered.notifyDataSetChanged();
                    }
                }));
                break;
        }
    }


    private void sortButtonClicked(View view) {
        LayoutInflater inflater = getLayoutInflater();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = inflater.inflate(R.layout.activity_sort_sheet, null);
        bottomSheetView = bottomSheetView.findViewById(R.id.bottomSheetSortContainer);

        bottomSheetView.findViewById(R.id.sort_by_title).setOnClickListener(view1 -> {

            if (titleSortedByAsc) {
                titleSortedByAsc = false;
                noteViewModel.fetchAllDescByCategory(categoryId).observe(getViewLifecycleOwner(), this::refreshNotes);
            } else {
                titleSortedByAsc = true;
                noteViewModel.fetchAllAscByCategory(categoryId).observe(getViewLifecycleOwner(), this::refreshNotes);
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.sort_by_created_date).setOnClickListener(view12 -> {
            if (createdDateSortedByAsc) {
                createdDateSortedByAsc = false;
                noteViewModel.fetchAllNotesOrderByDateDesc(categoryId).observe(getViewLifecycleOwner(), this::refreshNotes);
            } else {
                createdDateSortedByAsc = true;
                noteViewModel.fetchAllNotesOrderByDateAsc(categoryId).observe(getViewLifecycleOwner(), this::refreshNotes);
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
        RecyclerView noteFilterRecycle = binding.noteFilterRecycle;

        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesFiltered.clear();
                noteFilterRecycle.setLayoutManager(new GridLayoutManager(getContext(), 2));

                // filter category that contain x value
                notesFiltered = notes.stream().filter(note -> {
                    if (s.length() == 0) return false;
                    return note.getTitle().toLowerCase().contains(s.toString().toLowerCase()) || note.getDescription().toLowerCase().contains(s.toString().toLowerCase());
                }).collect(Collectors.toList());

                noteRecycleAdapter = new NoteRecycleAdapter(notesFiltered, getOnCallbackAdapter(notesFiltered));
            }

            @Override
            public void afterTextChanged(Editable s) {
                noteFilterRecycle.setAdapter(noteRecycleAdapter);
            }
        };
    }

    @NonNull
    private NoteRecycleAdapter.OnNoteCallback getOnCallbackAdapter(List<Note> notes) {
        return new NoteRecycleAdapter.OnNoteCallback() {
            @Override
            public void onNoteSelected(View view, int position) {
                Navigation.findNavController(view).navigate(NoteListFragmentDirections.actionNoteDetailActivity().setOldNote(notes.get(position)));

                Handler handler = new Handler();
                Runnable runnable = () -> searchView.hide();
                handler.postDelayed(runnable, 1000);

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
                View moveNoteView = inflater.inflate(R.layout.categories_dropdown, null);
                autoCompleteTextView = moveNoteView.findViewById(R.id.auto_complete_txt);

                adapterItems = new ArrayAdapter<>(getContext(), R.layout.categories_dropdown_items, categories);
                autoCompleteTextView.setAdapter(adapterItems);
                autoCompleteTextView.setOnItemClickListener((adapterView, view, position1, id) -> {
                    String category = adapterView.getItemAtPosition(position1).toString();

                    categoryViewModel.getCategoryByName(category).observe(getViewLifecycleOwner(), result -> {
                        note.setCategoryId(result.getId());

                    });
                });

                new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle("Move Note")
                        .setIcon(getResources().getDrawable(R.drawable.move, requireActivity().getTheme()))
                        .setMessage("Select the category you want to move your note.")
                        .setView(moveNoteView).setNegativeButton("Cancel", (dialog, which) -> {

                        }).setPositiveButton("Done", (dialog, which) -> {
                            noteViewModel.updateNote(note);
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

            @Override
            public void setCardBackgroundColor(View view, int position) {
                noteViewModel.fetchColorsByNoteId(notes.get(position).getNoteId()).observe(getViewLifecycleOwner(), noteColors -> {
                    List<Color> colors = new ArrayList<>();
                    noteColors.forEach(resultColors -> colors.addAll(resultColors.getColors()));
                    if (colors.size() > 0) {
                        if (!Objects.equals(colors.get(0).getColor(), "colorDefaultNoteColor")) {
                            view.setBackgroundColor(getSourceColor(view, colors.get(0).getColor()));
                        }
                    }
                });
            }
        };
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        //startActivityForResult(intent, SPEECH_REQUEST_CODE);
        textToSpeakLauncher.launch(intent);
    }

    public int getSourceColor(View view, String colorName) {
        if (!colorName.isEmpty()) {
            switch (colorName) {
                default:
                case "colorDefaultNoteColor":
                    return view.getResources().getColor(R.color.colorDefaultNoteColor, requireActivity().getTheme());
                case "colorNote2":
                    return view.getResources().getColor(R.color.colorNote2, requireActivity().getTheme());
                case "colorNote3":
                    return view.getResources().getColor(R.color.colorNote3, requireActivity().getTheme());
                case "colorNote4":
                    return view.getResources().getColor(R.color.colorNote4, requireActivity().getTheme());
                case "colorNote5":
                    return view.getResources().getColor(R.color.colorNote5, requireActivity().getTheme());
                case "colorNote6":
                    return view.getResources().getColor(R.color.colorNote6, requireActivity().getTheme());
                case "colorNote7":
                    return view.getResources().getColor(R.color.colorNote7, requireActivity().getTheme());
                case "colorNote8":
                    return view.getResources().getColor(R.color.colorNote8, requireActivity().getTheme());
            }
        }
        return 0;
    }
}
