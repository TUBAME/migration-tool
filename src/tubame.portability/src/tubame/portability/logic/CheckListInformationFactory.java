/*
 * CheckListInformationFactory.java
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
package tubame.portability.logic;

/**
 * Check list information file operation Factory.<br/>
 * Generate {@link CheckListInformationFacade}.<br/>
 */
public class CheckListInformationFactory {
    /**
     * Check list information file Facade
     */
    private static final CheckListInformationFacade FACADE = new CheckListInformationFacade();

    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     * 
     */
    private CheckListInformationFactory() {
        // no operation
    }

    /**
     * Get the checklist information file Facade.<br/>
     * Generate {@link CheckListInformationFacade}.<br/>
     * 
     * @return {@link CheckListInformationFacade}
     */
    public static CheckListInformationFacade getCheckListInformationFacade() {
        return CheckListInformationFactory.FACADE;
    }

}
