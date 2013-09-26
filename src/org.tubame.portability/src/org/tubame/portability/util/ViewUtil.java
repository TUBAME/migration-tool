/*
 * ViewUtil.java
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
package org.tubame.portability.util;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Utility class that specializes in EclipseView.<br/>
 * Set or get operation EclipseView properties etc.<br/>
 */
public class ViewUtil {
    /**
     * Default constructor.<br/>
     * Disable the instance generation from other class.<br/>
     * 
     */
    private ViewUtil() {
        // no operation
    }

    /**
     * Get the parent element at the top of the Node that you specify.<br/>
     * 
     * @param findParentTarget
     *            Node
     * @return The top-level node
     */
    public static TreeNode getParent(TreeNode findParentTarget) {
        boolean isParent = true;
        TreeNode parent = findParentTarget;
        while (isParent) {
            TreeNode temp = parent.getParent();
            if (temp == null) {
                isParent = false;
            } else {
                parent = temp;
            }
        }
        return parent;
    }
}
