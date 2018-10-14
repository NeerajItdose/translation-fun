/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.tristan.heinig.translationfun.database;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.tristan.heinig.translationfun.util.LiveDataTestUtil;
import android.tristan.heinig.translationfun.database.dao.TranslationDao;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the implementation of {@link TranslationDao}
 */
@RunWith(AndroidJUnit4.class)
public class TranslationDaoTest {

  // executes each task synchronously
  @Rule
  public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
  private TranslationDatabase mDatabase;
  private TranslationDao mTranslationDao;

  private TranslationItem translationItemNewerLessViews;
  private TranslationItem translationItemOlderMoreViews;

  @Before
  public void initDb() throws Exception {
    // using an in-memory database because the information stored here disappears when the
    // process is killed
    mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), TranslationDatabase.class)
      // allowing main thread queries, just for testing
      .allowMainThreadQueries().build();

    mTranslationDao = mDatabase.mTranslationDao();
    long mTimeMillis = System.currentTimeMillis();
    translationItemNewerLessViews = buildTranslationItem("1", "", "", "", mTimeMillis, 1);
    translationItemOlderMoreViews = buildTranslationItem("2", "", "", "", mTimeMillis - 1000, 10);
  }

  @After
  public void closeDb() throws Exception {
    mDatabase.close();
  }

  @Test
  public void getEmptyTranslationList() throws InterruptedException {
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAll());
    assertTrue(translationItems.isEmpty());
  }

  @Test
  public void insertTranslationsAndDeleteAllTranslations() throws InterruptedException {
    mTranslationDao.insert(translationItemNewerLessViews);
    mTranslationDao.insert(translationItemOlderMoreViews);
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate());
    assertEquals(2, translationItems.size());
    mTranslationDao.deleteAll();
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate());
    assertEquals(0, translationItems.size());
  }

  @Test
  public void getTranslationOrderedByDate() throws InterruptedException {
    mTranslationDao.insert(translationItemNewerLessViews);
    mTranslationDao.insert(translationItemOlderMoreViews);
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate());
    assertEquals(2, translationItems.size());
    assertTrue(translationItems.get(0).getDate().after(translationItems.get(1).getDate()));
    // change order of inserted items and test again
    mTranslationDao.deleteAll();
    mTranslationDao.insert(translationItemOlderMoreViews);
    mTranslationDao.insert(translationItemNewerLessViews);
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate());
    assertEquals(2, translationItems.size());
    assertTrue(translationItems.get(0).getDate().after(translationItems.get(1).getDate()));
  }

  @Test
  public void getTranslationOrderedByViews() throws InterruptedException {
    mTranslationDao.insert(translationItemNewerLessViews);
    mTranslationDao.insert(translationItemOlderMoreViews);
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByViews());
    assertEquals(2, translationItems.size());
    assertTrue(translationItems.get(0).getViews() > translationItems.get(1).getViews());
    // change order of inserted items and test again
    mTranslationDao.deleteAll();
    mTranslationDao.insert(translationItemOlderMoreViews);
    mTranslationDao.insert(translationItemNewerLessViews);
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByViews());
    assertEquals(2, translationItems.size());
    assertTrue(translationItems.get(0).getViews() > translationItems.get(1).getViews());
  }

  @Test
  public void getTranslationOrderedByDateWithLimit() throws InterruptedException {
    long mTimeMillis = translationItemNewerLessViews.getDate().getTime();
    mTranslationDao.insert(translationItemNewerLessViews);
    mTranslationDao.insert(translationItemOlderMoreViews);
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostRecent(1));
    assertEquals(1, translationItems.size());
    assertEquals(mTimeMillis, translationItems.get(0).getDate().getTime());
    // change order of inserted items and test again
    mTranslationDao.deleteAll();
    mTranslationDao.insert(translationItemOlderMoreViews);
    mTranslationDao.insert(translationItemNewerLessViews);
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostRecent(1));
    assertEquals(1, translationItems.size());
    assertEquals(mTimeMillis, translationItems.get(0).getDate().getTime());
  }

  @Test
  public void getTranslationOrderedByViewsWithLimit() throws InterruptedException {
    mTranslationDao.insert(translationItemNewerLessViews);
    mTranslationDao.insert(translationItemOlderMoreViews);
    List<TranslationItem> translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostViewed(1));
    assertEquals(1, translationItems.size());
    assertEquals(10, translationItems.get(0).getViews());
    // change order of inserted items and test again
    mTranslationDao.deleteAll();
    mTranslationDao.insert(translationItemOlderMoreViews);
    mTranslationDao.insert(translationItemNewerLessViews);
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostViewed(1));
    assertEquals(1, translationItems.size());
    assertEquals(10, translationItems.get(0).getViews());
  }

  @Test
  public void insertTranslationAndGetTranslationByTextAndDeleteByText() {
    String text = "text";
    translationItemNewerLessViews.setText(text);
    mTranslationDao.insert(translationItemNewerLessViews);
    TranslationItem translationItemDB = mTranslationDao.getByText(text);
    assertNotNull(translationItemDB);
    assertEquals(text, translationItemDB.getText());
    mTranslationDao.delete(text);
    translationItemDB = mTranslationDao.getByText(text);
    assertNull(translationItemDB);
  }

  @Test
  public void updateTranslation() {
    String text = "text";
    String translation = "translation";
    translationItemNewerLessViews.setText(text);
    translationItemNewerLessViews.setTranslation(translation);
    mTranslationDao.insert(translationItemNewerLessViews);
    TranslationItem translationItemDB = mTranslationDao.getByText(text);
    assertEquals(translation, translationItemDB.getTranslation());
    String updatedTranslation = "updated translation";
    translationItemDB.setTranslation(updatedTranslation);
    mTranslationDao.update(translationItemDB);
    translationItemDB = mTranslationDao.getByText(text);
    assertEquals(updatedTranslation, translationItemDB.getTranslation());
  }

  private TranslationItem buildTranslationItem(String pText, String pTranslation, String pSourceLngCode, String pTargetLngCode, long pDate, int pViews) {
    return new TranslationItem(pText, pTranslation, pSourceLngCode, pTargetLngCode, new Date(pDate), pViews);
  }
}
