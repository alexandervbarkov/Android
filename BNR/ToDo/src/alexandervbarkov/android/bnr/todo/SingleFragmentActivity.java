package alexandervbarkov.android.bnr.todo;



import alexandervbarkov.android.bnr.todo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();
	
	protected int getLayoutResId() {
		return R.layout.activity_fragment_container;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(UtilsSettings.getTheme(getApplication()))
			setTheme(R.style.dark_theme);
		else
			setTheme(R.style.AppTheme);
		
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutResId());
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.cont_fragment);
		
		if(f == null) {
			f = createFragment();
			fm.beginTransaction().add(R.id.cont_fragment, f).commit();
		}
	}
}