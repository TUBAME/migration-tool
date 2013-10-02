/*
 * MousePointGetTreeViewer.java
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
package tubame.portability.plugin.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Mouse click management tree view class.<br/>
 * Mouse click after the class that holds the mouse coordinates. <br/>
 * It is used in the search results editing screen.<br/>
 */
public class MousePointGetTreeViewer extends TreeViewer {

    /**
     * Right mouse click coordinate
     */
    private final Point mouse = new Point(0, 0);

    /**
     * Constructor.<br/>
     * 
     * @see TreeViewer
     * 
     * @param parent
     *            Parent element
     * @param style
     *            Style
     */
    public MousePointGetTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void hookControl(Control control) {
        control.addMouseListener(new MouseListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // no operation
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDown(MouseEvent mouseEvent) {
                mouse.x = mouseEvent.x;
                mouse.y = mouseEvent.y;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseUp(MouseEvent e) {
                // no operation
            }
        });
        super.hookControl(control);
    }

    /**
     * Get the coordinates you right-click on the view.<br/>
     * 
     * @return Right mouse click coordinate
     */
    public Point getMouseClickPoint() {
        return mouse;
    }
}
