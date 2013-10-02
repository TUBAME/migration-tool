/*
 * ConvertState.java
 * Created on 2013/06/28
 *
 * Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation
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
package tubame.portability.logic.convert;

/**
 * Manage statically the conversion state.
 */
public class ConvertState extends State {
    /**
     * Conversion state
     */
    private static ConvertState instance = new ConvertState();

    /**
     * Default constructor.<br/>
     * It is not possible to generate an instance from another class.<br/>
     * 
     */
    private ConvertState() {
        super();
    }

    /**
     * Get instance.<br/>
     * 
     * @return Instance
     */
    public static State getInstance() {
        return instance;
    }
}
