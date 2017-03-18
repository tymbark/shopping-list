package com.damianmichalak.shopping_list.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventsWrapper {

    public interface EventsListener {
        void onDataChange(DataSnapshot dataSnapshot);

        void onCancelled(DatabaseError databaseError);
    }

    private EventsListener eventsListener;

    public void pushEventOnDataChange(@Nonnull final DataSnapshot dataSnapshot) {
        if (eventsListener != null) {
            eventsListener.onDataChange(dataSnapshot);
        }
    }

    public void pushEventDatabaseError(@Nonnull final DatabaseError databaseError) {
        if (eventsListener != null) {
            eventsListener.onCancelled(databaseError);
        }
    }

    private final ValueEventListener firebaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (eventsListener != null) {
                eventsListener.onDataChange(dataSnapshot);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            if (eventsListener != null) {
                eventsListener.onCancelled(databaseError);
            }
        }
    };

    public void setEventsListener(EventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    public ValueEventListener getFirebaseListener() {
        return firebaseListener;
    }

    public EventsListener getEventsListener() {
        return eventsListener;
    }
}
