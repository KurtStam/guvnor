/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.decisiontable.analysis.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

public class NumericShortConditionDetector extends ConditionDetector<NumericShortConditionDetector> {

    // TODO support operator "in" and optimize to allowedValueList if not continuous
    // private List<Short> allowedValueList = null;
    private Short       from           = null;
    private boolean     fromInclusive;
    private Short       to             = null;
    private boolean     toInclusive;
    private List<Short> disallowedList = new ArrayList<Short>( 1 );

    public NumericShortConditionDetector(Pattern52 pattern,
                                         String factField,
                                         Short value,
                                         String operator) {
        super( pattern,
               factField );
        if ( operator.equals( "==" ) ) {
            from = value;
            fromInclusive = true;
            to = value;
            toInclusive = true;
        } else if ( operator.equals( "!=" ) ) {
            disallowedList.add( value );
        } else if ( operator.equals( "<" ) ) {
            to = value;
            toInclusive = false;
        } else if ( operator.equals( "<=" ) ) {
            to = value;
            toInclusive = true;
        } else if ( operator.equals( ">" ) ) {
            from = value;
            fromInclusive = false;
        } else if ( operator.equals( ">=" ) ) {
            from = value;
            fromInclusive = true;
        } else {
            hasUnrecognizedConstraint = true;
        }
    }

    public NumericShortConditionDetector(NumericShortConditionDetector a,
                                         NumericShortConditionDetector b) {
        super( a,
               b );
        if ( b.from == null ) {
            from = a.from;
            fromInclusive = a.fromInclusive;
        } else if ( a.from == null ) {
            from = b.from;
            fromInclusive = b.fromInclusive;
        } else {
            int comparison = a.from.compareTo( b.from );
            if ( comparison < 0 ) {
                from = b.from;
                fromInclusive = b.fromInclusive;
            } else if ( comparison == 0 ) {
                from = a.from;
                fromInclusive = a.fromInclusive && b.fromInclusive;
            } else {
                from = a.from;
                fromInclusive = a.fromInclusive;
            }
        }
        if ( b.to == null ) {
            to = a.to;
            toInclusive = a.toInclusive;
        } else if ( a.to == null ) {
            to = b.to;
            toInclusive = b.toInclusive;
        } else {
            int comparison = a.to.compareTo( b.to );
            if ( comparison < 0 ) {
                to = a.to;
                toInclusive = a.toInclusive;
            } else if ( comparison == 0 ) {
                to = a.to;
                toInclusive = a.toInclusive && b.toInclusive;
            } else {
                to = b.to;
                toInclusive = b.toInclusive;
            }
        }
        disallowedList.addAll( a.disallowedList );
        disallowedList.addAll( b.disallowedList );
        optimizeNotList();
        detectImpossibleMatch();
    }

    private void optimizeNotList() {
        for ( Iterator<Short> notIt = disallowedList.iterator(); notIt.hasNext(); ) {
            Short notValue = notIt.next();
            if ( from != null ) {
                int comparison = notValue.compareTo( from );
                if ( comparison <= 0 ) {
                    notIt.remove();
                }
                if ( comparison == 0 ) {
                    fromInclusive = false;
                }
            }
            if ( to != null ) {
                int comparison = notValue.compareTo( to );
                if ( comparison >= 0 ) {
                    notIt.remove();
                }
                if ( comparison == 0 ) {
                    toInclusive = false;
                }
            }
        }
    }

    private void detectImpossibleMatch() {
        if ( from != null && to != null ) {
            int comparison = from.compareTo( to );
            if ( comparison > 0 || (comparison == 0 && (!fromInclusive || !toInclusive)) ) {
                impossibleMatch = true;
            }
        }
    }

    public NumericShortConditionDetector merge(NumericShortConditionDetector other) {
        return new NumericShortConditionDetector( this,
                                                  other );
    }

}
