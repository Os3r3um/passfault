/* ©Copyright 2011 Cameron Morris
 *
 * This file is part of Passfault.
 *
 * Passfault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Passfault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Passfault.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.passfault.dictionary;

import com.passfault.MockPasswordResults;
import com.passfault.PasswordPattern;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import static junit.framework.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MisspellingFinderInMemoryTest {

  private static DictionaryPatternsFinder finder;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    InMemoryDictionary dictionary = InMemoryDictionary.newInstance("./test/tiny-lower.words", false, "tiny-lower");
    finder = new DictionaryPatternsFinder(dictionary, new MisspellingStrategy(1));
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void findWord() throws Exception {
    System.out.println("findWord");
    MockPasswordResults p = new MockPasswordResults("passwerd");
    finder.analyze(p);
    List<PasswordPattern> patterns = p.getFoundPatterns();
    Collection<PasswordPattern> oneLength = getPatternsOfALength(patterns, 8);
    assertEquals(1, oneLength.size());
  }

  @Test
  public void findWord_garbageinfront() throws Exception {
    System.out.println("findWord_garbageinfront");
    MockPasswordResults p = new MockPasswordResults("1234passwerd");
    finder.analyze(p);
    List<PasswordPattern> patterns = p.getFoundPatterns();
    Collection<PasswordPattern> oneLength = getPatternsOfALength(patterns, 8);
    assertEquals(1, oneLength.size());
  }

  @Test
  public void findWord_garbageinback() throws Exception {
    {
      System.out.println("findWord_garbageinback");
      MockPasswordResults p = new MockPasswordResults("wisp");
      finder.analyze(p);
      assertTrue(p.getPossiblePatternCount() > 1);
    }
    {
      MockPasswordResults p = new MockPasswordResults("wisp1");
      finder.analyze(p);
      assertTrue(p.getPossiblePatternCount() > 1);
    }
    {
      MockPasswordResults p = new MockPasswordResults("wisp12");
      finder.analyze(p);
      assertTrue(p.getPossiblePatternCount() > 1);
    }
    {
      MockPasswordResults p = new MockPasswordResults("wisp123");
      finder.analyze(p);
      assertTrue(p.getPossiblePatternCount() > 1);
    }
  }

  @Test
  public void findNonWord() throws Exception {
    System.out.println("findNonWord");
    MockPasswordResults p = new MockPasswordResults("qqq");
    finder.analyze(p);
    assertEquals(0, p.getPossiblePatternCount());
  }

  @Test
  public void findMultiWords() throws Exception {
    System.out.println("findMultiWords");
    MockPasswordResults p = new MockPasswordResults("passwerdpasswerd");//dictionary
    finder.analyze(p);
    List<PasswordPattern> patterns = p.getFoundPatterns();
    Collection<PasswordPattern> oneLength = getPatternsOfALength(patterns, 8);
    assertEquals(2, oneLength.size());
  }

  @Test
  public void testLength() throws Exception {
    System.out.println("findMultiWords");
    MockPasswordResults p = new MockPasswordResults("passwerd");
    finder.analyze(p);
    List<PasswordPattern> patterns = p.getFoundPatterns();
    Collection<PasswordPattern> oneLength = getPatternsOfALength(patterns, 8);
    assertEquals(1, oneLength.size());
  }

  public Collection<PasswordPattern> getPatternsOfALength(Collection<PasswordPattern> patterns, int length) {
    LinkedList<PasswordPattern> toReturn = new LinkedList<PasswordPattern>();

    for (PasswordPattern pattern : patterns) {
      if (pattern.getLength() == length) {
        toReturn.add(pattern);
      }
    }
    return toReturn;
  }
}