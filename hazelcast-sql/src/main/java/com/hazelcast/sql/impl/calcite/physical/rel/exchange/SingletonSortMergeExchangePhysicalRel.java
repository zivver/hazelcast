/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.calcite.physical.rel.exchange;

import com.hazelcast.sql.impl.calcite.physical.rel.PhysicalRel;
import com.hazelcast.sql.impl.calcite.physical.rel.PhysicalRelVisitor;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.SingleRel;

import java.util.List;

/**
 * Exchange which marge sorted input streams from several nodes into a a single sorted stream on a single node.
 * <p>
 * Traits:
 * <ul>
 *     <li><b>Collation</b>: derived from the input, never empty</li>
 *     <li><b>Distribution</b>: SINGLETON</li>
 * </ul>
 */
public class SingletonSortMergeExchangePhysicalRel extends SingleRel implements PhysicalRel {
    /** Collation. */
    private final RelCollation collation;

    public SingletonSortMergeExchangePhysicalRel(
        RelOptCluster cluster,
        RelTraitSet traitSet,
        RelNode input,
        RelCollation collation
    ) {
        super(cluster, traitSet, input);

        this.collation = collation;
    }

    public RelCollation getCollation() {
        return collation;
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new SingletonSortMergeExchangePhysicalRel(getCluster(), traitSet, sole(inputs), collation);
    }

    @Override
    public void visit(PhysicalRelVisitor visitor) {
        ((PhysicalRel) input).visit(visitor);

        visitor.onSingletonSortMergeExchange(this);
    }

    @Override
    public final RelWriter explainTerms(RelWriter pw) {
        super.explainTerms(pw);

        return pw.item("collation", collation.getFieldCollations());
    }
}
