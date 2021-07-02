package tk.augmeneco.augcalcandroid;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class KeypadCollectionAdapter extends FragmentStateAdapter {
    public KeypadCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new KeypadFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(KeypadFragment.ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return KeypadFragment.fragmentNumber;
    }
}
