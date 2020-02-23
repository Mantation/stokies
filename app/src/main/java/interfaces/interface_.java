package interfaces;

public class interface_ {
    public interface selectNavItem {
        void onContact(int id);
    }
    public interface setHomeToolBar {
        void onChangeTitle(String TiTle);
    }

    public interface DrawableClickListener {

        public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
        public void onClick(DrawablePosition target);
    }

    public interface ICheckChangeListener {
        void onItemChecked(int position, boolean value);
    }
}
