package com.imangazaliev.slugify;

import android.os.Build;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, manifest = Config.NONE)
public abstract class BaseTest {

    @Before
    public void setup() throws Exception {
        onSetup();
    }

    protected abstract void onSetup() throws URISyntaxException, Exception;


}
