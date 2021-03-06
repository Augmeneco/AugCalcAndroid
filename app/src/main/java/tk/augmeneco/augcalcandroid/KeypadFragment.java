package tk.augmeneco.augcalcandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Instances of this class are fragments representing a single
// object in our collection.
public class KeypadFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    public static final int fragmentNumber = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        switch (args.getInt(ARG_POSITION)) {
            case 0:
                return inflater.inflate(R.layout.main_keypad, container, false);
            case 1:
                return inflater.inflate(R.layout.extended_keypad, container, false);
            default:
                return inflater.inflate(R.layout.main_keypad, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args.getInt(ARG_POSITION) == 0) {
            getView().findViewById(R.id.buttonRemove).setOnTouchListener(
                    new RepeatListener(700, 100, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity)getActivity()).onRemoveButtonClick(view);
                        }
                    }));
        }
    }
}

