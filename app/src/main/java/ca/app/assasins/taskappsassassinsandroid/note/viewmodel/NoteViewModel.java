package ca.app.assasins.taskappsassassinsandroid.note.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ca.app.assasins.taskappsassassinsandroid.common.model.Audio;
import ca.app.assasins.taskappsassassinsandroid.common.model.Picture;
import ca.app.assasins.taskappsassassinsandroid.note.model.Note;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteAudios;
import ca.app.assasins.taskappsassassinsandroid.note.model.NoteImages;
import ca.app.assasins.taskappsassassinsandroid.note.repositories.NoteRepository;
import ca.app.assasins.taskappsassassinsandroid.task.model.Task;
import ca.app.assasins.taskappsassassinsandroid.task.model.TaskImages;

public class NoteViewModel extends ViewModel {

    private final NoteRepository noteRepository;

    public NoteViewModel(Application application) {
        noteRepository = new NoteRepository(application);
    }

    public void createNote(@NonNull Note note) {
        noteRepository.save(note);
    }

    public void updateNote(@NonNull Note note) {
        noteRepository.update(note);
    }

    public LiveData<List<Note>> getNoteByTitle(String title) {
        return noteRepository.fetchByTitle(title);
    }

    public LiveData<List<Note>> fetchAllNoteByCategory(Long categoryId) {
        return noteRepository.fetchAllNoteByCategory(categoryId);
    }

    public void deleteNote(@NonNull Note note) {
        noteRepository.delete(note);
    }

    public void saveNoteWithPictures(Note note, List<Picture> pictures) {
        noteRepository.saveNoteWithPictures(note, pictures);
    }

    public void updateNoteWithPictures(Note note, List<Picture> pictures) {
        noteRepository.updateNoteWithPictures(note, pictures);
    }

    public LiveData<List<NoteImages>> fetchPicturesByNoteId(long noteId) {
        return noteRepository.fetchPicturesByNoteId(noteId);
    }

    public LiveData<NoteImages> findPictureByNoteId(long noteId) {
        return noteRepository.findPictureByNoteId(noteId);
    }

    public LiveData<List<NoteAudios>> fetchAudiosByNote(long noteId) {
        return noteRepository.fetchAudiosByNoteId(noteId);
    }

    public void deletePicture(@NonNull Picture picture) {
        this.noteRepository.deletePicture(picture);
    }

    public void saveNoteWithPicturesAudios(Note newNote, List<Picture> myPictures, List<Audio> mAudios) {
        noteRepository.saveNoteWithPicturesAudios(newNote, myPictures, mAudios);
    }

    public void deleteAudio(@NonNull Audio audio) {
        noteRepository.deleteAudio(audio);
    }
}
