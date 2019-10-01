package com.rove.notestick.Calendar;

import android.app.Application;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.rove.datalayer.Data.Entity_Note;
import com.rove.domainlayer.Repository.NoteRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CalendarViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private Application mApplication;
    private LiveData<List<Entity_Note>> LocNotesOnDate; // triggers every time database changes
    private MediatorLiveData<List<Entity_Note>> notesOnDate; // This will observe LocNotesOnDate and trigger it whenever necessory
    private MutableLiveData<Calendar> withDate;
    private boolean needNotesOnDate = false;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        noteRepository = new NoteRepository(application);
        withDate = new MutableLiveData<>();
        notesOnDate = new MediatorLiveData<>();
        LocNotesOnDate = Transformations.switchMap(withDate, input -> {
            Log.d("=>","notesOnDate switch map from construct worked");
            return noteRepository.getAllNoteswithDate(input.getTime());

        });
        notesOnDate.addSource(LocNotesOnDate, entity_notes -> {
            if(needNotesOnDate){
                notesOnDate.setValue(entity_notes);
                needNotesOnDate = false;
            }

        });

    }

    LiveData<List<Date>> getRelavantDates(){
        return noteRepository.getAllDateswithNotes();
    }

    public Entity_Note getNotWithId(int noteId) throws ExecutionException, InterruptedException {
        Entity_Note note = noteRepository.getNoteByID(noteId);
        return note;
    }

    public void showNotesOnDate(Calendar calendar) {
        Log.d("=>","showNotesOnDate called");
            needNotesOnDate = true;
            withDate.setValue(calendar);

    }

    public LiveData<List<Entity_Note>> getNotesOnDate() {
        Log.d("=>","getNotesOnDate called");
        return notesOnDate;
    }

//    public void resetData() {
//        Log.d("=>","resetData called");
//        withDate = new MutableLiveData<>();
//        //notesOnDate = Transformations.switchMap(withDate,calendar->noteRepository.getAllNoteswithDate(calendar.getTime()));
//        notesOnDate = null;
//        notesOnDate = Transformations.switchMap(withDate, new Function<Calendar, LiveData<List<Entity_Note>>>() {
//            @Override
//            public LiveData<List<Entity_Note>> apply(Calendar input) {
//                Log.d("=>","notesOnDate switch map from reset worked");
//                return noteRepository.getAllNoteswithDate(input.getTime());
//
//            }
//        });
//    }
}
