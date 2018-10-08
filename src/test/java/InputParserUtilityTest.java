/**
 * Copyright © 2016-2018 The Thingsboard Authors
 * Modifications © 2017-2018 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.hashmapinc.tempus.InputParserUtility;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Collections;

public class InputParserUtilityTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void validateCorrectJson() throws IOException {
        InputParserUtility inputParserUtility = new InputParserUtility();
        String jsonStr = "{\"id\":\"1\", \"ts\":\"45676\"}";
        Assert.assertEquals(inputParserUtility.validateJson(jsonStr),true);
    }

    @Test
    public void validateInCorrectJsonThrowsException() throws IOException {
        exception.expect(JsonEOFException.class);
        InputParserUtility inputParserUtility = new InputParserUtility();
        String jsonStr = "{\"id\":\"1\", \"ts\":\"45676}";
        inputParserUtility.validateJson(jsonStr);
    }

    @Test
    public void validateCorrectJsonWithDsAndTs() throws IOException {
        InputParserUtility inputParserUtility = new InputParserUtility();
        String jsonStr = "{\"id\":\"1\", \"ts\":\"45676\", \"ds\":\"3000\"}";
        Assert.assertEquals(inputParserUtility.validateJson(jsonStr, Collections.EMPTY_LIST),false);

    }

    @Test
    public void validateCorrectJsonWithAddedKey() throws IOException {
        InputParserUtility inputParserUtility = new InputParserUtility();
        String jsonStr = "{\"id\":\"1\", \"ts\":\"45676\", \"key\":\"val\"}";
        Assert.assertEquals(inputParserUtility.validateJson(jsonStr, Collections.singletonList("key")),true);
    }

    @Test
    public void validateCorrectJsonWithoutAddedKey() throws IOException {
        InputParserUtility inputParserUtility = new InputParserUtility();
        String jsonStr = "{\"id\":\"1\", \"ts\":\"45676\"}";
        Assert.assertEquals(inputParserUtility.validateJson(jsonStr, Collections.singletonList("key")),false);
    }
}
