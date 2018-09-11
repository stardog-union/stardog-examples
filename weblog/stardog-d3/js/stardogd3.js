'use strict';

function StardogD3(_selector, _options) {
    let container, node, nodes, relationship, relationshipOutline, relationshipOverlay, relationshipText,
        relationships, selector, simulation, svg, svgNodes, svgRelationships, svgScale, svgTranslate,
        justLoaded = false,
        numClasses = 0,
        options = {
            classes2colors: {},
            colors: colors(),
            images: undefined,
            infoPanel: true,
            minCollision: undefined,
            stardogData: undefined,
            nodeOutlineFillColor: undefined,
            nodeRadius: 25,
            onNodeClick: undefined,
            relationshipColor: '#a5abb6',
            relationshipOpacity: '.5',
            showRelationshipTitles: false,
            showRelationshipCurvedEdges: false,
            alwaysShowCurvedEdges: false,
            relationshipIcon: undefined,
            relationshipIconSize: 6,
            relationshipOverlay: false,
            relationshipOverlayColor: '#2660d3',
            zoomFit: false,
            showReasoning: false,
        },
        VERSION = '0.0.1';

    const endCaps = [
        {id: 0, name: 'circle', path: 'M 0, 0  m -5, 0  a 5,5 0 1,0 10,0  a 5,5 0 1,0 -10,0', viewbox: '-6 -6 12 12'},
        {id: 1, name: 'square', path: 'M 0,0 m -5,-5 L 5,-5 L 5,5 L -5,5 Z', viewbox: '-5 -5 10 10'},
        {id: 2, name: 'arrow', path: 'M 0,0 m -5,-5 L 5,0 L -5,5 Z', viewbox: '-5 -5 10 10'},
        {id: 2, name: 'stub', path: 'M 0,0 m -1,-5 L 1,-5 L 1,5 L -1,5 Z', viewbox: '-1 -5 2 10'}
    ];

    function init(_selector, _options) {

        if (!Array.prototype.find) {
            Array.prototype.find = function (predicate) {
                if (this == null) {
                    throw new TypeError('Array.prototype.find called on null or undefined');
                }
                if (typeof predicate !== 'function') {
                    throw new TypeError('predicate must be a function');
                }
                let list = Object(this);
                let length = list.length >>> 0;
                let thisArg = arguments[1];
                let value;

                for (let i = 0; i < length; i++) {
                    value = list[i];
                    if (predicate.call(thisArg, value, i, list)) {
                        return value;
                    }
                }
                return undefined;
            };
        }

        merge(options, _options);

        if (options.icons) {
            options.showIcons = true;
        }

        if (!options.minCollision) {
            options.minCollision = options.nodeRadius * 2;
        }

        selector = _selector;

        container = d3.select(selector);

        container.attr('class', 'stardogd3')
            .html('');

        appendGraph(container);

        simulation = initSimulation();

        if (options.stardogData) {
            loadStardogData(options.stardogData);
        } else {
            console.error('Error: stardogData must be specified!');
        }
    }

    function initSimulation() {

        let simulation = d3.forceSimulation(nodes)
            .force('collide', d3.forceCollide().radius(function(d) {
                return options.minCollision;
            }))
            // .force('charge', d3.forceManyBody().strength(200))
            .force('link', d3.forceLink(relationships).distance(130).id(function(d) {
                return d.id;
            }))
            .force('center', d3.forceCenter(d3.select(_selector).node().offsetWidth / 2, d3.select(_selector).node().offsetHeight / 2))
            .on('tick', function() {
                tick();
            })
            .on('end', function() {
                if (options.zoomFit && !justLoaded) {
                    justLoaded = true;
                    zoomFit(2);
                }
            });

        return simulation;
    }

    function loadStardogData() {
        nodes = [];
        relationships = [];

        updateWithStardogData(options.stardogData);
    }

    function stickNode(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }

    function tick() {
        tickNodes();
        tickRelationships();
    }

    function tickNodes() {
        if (node) {
            node.attr('transform', function (d) {
                return 'translate(' + d.x + ', ' + d.y + ')';
            });
        }
    }

    function tickRelationships() {
        if (relationship) {
            relationship.attr('transform', function (d) {
                let angle = rotation(d.source, d.target);
                return 'translate(' + d.source.x + ', ' + d.source.y + ') rotate(' + angle + ')';
            });



            tickRelationshipsTexts();
            tickRelationshipsOutlines();
            tickRelationshipsOverlays();
        }
    }

    /**
     * Updates the location of the relationship edges. This will check to see what type of edges
     * will be used based upon the 'showRelationshipCurvedEdges' and 'alwaysShowCurvedEdges' options.
     * The code at the following scenarios
     * showRelationshipCurvedEdges/alwaysShowCurvedEdges are on - only curved lines will be used for edges
     * showRelationshipCurvedEdges on but showRelationshipCurvedEdges off - curved edges will be used when there
     *  are multiple links between the nodes. If there is only a single edge between the node, a straight line
     *  will represent the edge
     * showRelationshipCurvedEdges is off - only straight lines will be used for edges
     */
    function tickRelationshipsOutlines() {

        relationship.each(function (relationship) {

            let rel = d3.select(this),
                outline = rel.select('.outline');

            // Determines if curved edges should always be used
            let opposingRelationship = options.showRelationshipCurvedEdges && options.alwaysShowCurvedEdges;

            // If only showRelationshipCurvedEdges is set, we will check to see if there are multiple
            // edges in order to draw a curved or straight edge.
            if (!opposingRelationship && options.showRelationshipCurvedEdges) {
                opposingRelationship = relationships.find(r =>
                    r.source.id === relationship.target.id &&
                    r.target.id === relationship.source.id);
            }

            if (opposingRelationship) {
                outline.attr('d', function (d) {

                    let offset = 30;

                    let dx = (d.target.x - d.source.x);
                    let dy = (d.target.y - d.source.y);

                    let normalise = Math.sqrt((dx * dx) + (dy * dy));

                    return "M " + options.nodeRadius + ",0S" + ((normalise / 2) * .96)  + "," + offset + " " + (normalise - options.nodeRadius) + ",0";
                });
            } else {

                outline.attr('d', function (d) {

                    let dx = (d.target.x - d.source.x);
                    let dy = (d.target.y - d.source.y);

                    let normalise = Math.sqrt((dx * dx) + (dy * dy));

                    // Alters the length of the edge depending upon the relationship icon
                    let nodeRadius = options.nodeRadius;
                    if (options.relationshipIcon && endCaps.find(e => e.name === options.relationshipIcon)) {
                        if (options.relationshipIcon !== 'stub') {
                            nodeRadius += (options.relationshipIconSize / 2);
                        } else {
                            nodeRadius += 1;
                        }
                    }

                    return "M " + (options.nodeRadius + 1) + " 0 L " + (normalise - nodeRadius) + " 0";
                });
            }
        });
    }

    function tickRelationshipsOverlays() {

        relationshipOverlay.attr('d', function (d) {

            // Determines if curved edges should always be used
            let opposingRelationship = options.showRelationshipCurvedEdges && options.alwaysShowCurvedEdges;

            // If only showRelationshipCurvedEdges is set, we will check to see if there are multiple
            // edges in order to draw a curved or straight edge.
            if (options.showRelationshipCurvedEdges) {
                opposingRelationship = relationships.find(r =>
                    r.source.id === d.target.id &&
                    r.target.id === d.source.id);
            }

            if (opposingRelationship) {

                let offset = 30;

                let dx = (d.target.x - d.source.x);
                let dy = (d.target.y - d.source.y);

                let normalise = Math.sqrt((dx * dx) + (dy * dy));

                return "M" + (options.nodeRadius - 1) +
                    ",0S" + ((normalise / 2) * .96)  + "," + offset + " " + (normalise - (options.nodeRadius - 1)) + ",0";
            } else {

                let center = {x: 0, y: 0},
                    angle = rotation(d.source, d.target),
                    n1 = unitaryNormalVector(d.source, d.target),
                    n = unitaryNormalVector(d.source, d.target, 50),
                    rotatedPointA = rotatePoint(center, {x: 0 - n.x, y: 0 - n.y}, angle),
                    rotatedPointB = rotatePoint(center, {
                        x: d.target.x - d.source.x - n.x,
                        y: d.target.y - d.source.y - n.y
                    }, angle),
                    rotatedPointC = rotatePoint(center, {
                        x: d.target.x - d.source.x + n.x - n1.x,
                        y: d.target.y - d.source.y + n.y - n1.y
                    }, angle),
                    rotatedPointD = rotatePoint(center, {x: 0 + n.x - n1.x, y: 0 + n.y - n1.y}, angle);

                return 'M ' + options.nodeRadius + ' ' + (rotatedPointA.y - 1) +
                    ' L ' + (rotatedPointB.x - options.nodeRadius) + ' ' + (rotatedPointB.y - 1) +
                    ' L ' + (rotatedPointC.x - options.nodeRadius)+ ' ' + rotatedPointC.y +
                    ' L ' + options.nodeRadius + ' ' + rotatedPointD.y +
                    ' Z';
            }
        });
    }

    function tickRelationshipsTexts() {

        relationship.each(function (relationship) {

            let rel = d3.select(this),
                text = rel.select('.text');

            let opposingRelationship;
            if (options.showRelationshipCurvedEdges) {
                opposingRelationship = relationships.find(r =>
                    r.source.id === relationship.target.id &&
                    r.target.id === relationship.source.id
                );
            }

            if (opposingRelationship) {

                text.attr('transform', function (d) {

                    let angle = (rotation(d.source, d.target) + 360) % 360;

                    let dx = d.source.x - d.target.x;
                    let dy = d.source.y - d.target.y;

                    let normalise = Math.sqrt((dx * dx) + (dy * dy));

                    let rotateAngle = 360 - angle;

                    let translateY = 10;
                    if (rotateAngle > 350) {
                        translateY += (10 - (360 - rotateAngle)) / 2;

                    } else if (rotateAngle < 10) {
                        translateY += (10 - rotateAngle) / 2;
                    }

                    return 'translate(' + ((normalise + (options.nodeRadius)) / 2) + ', ' + translateY + ') rotate(' + rotateAngle + ')';
                });
            } else {

                text.attr('transform', function (d) {

                    let angle = (rotation(d.source, d.target) + 360) % 360,
                        mirror = angle > 90 && angle < 270,
                        center = {x: 0, y: 0},
                        n = unitaryNormalVector(d.source, d.target),
                        nWeight = mirror ? 2 : -3,
                        point = {
                            x: (d.target.x - d.source.x) * 0.5 + n.x * nWeight,
                            y: (d.target.y - d.source.y) * 0.5 + n.y * nWeight
                        },
                        rotatedPoint = rotatePoint(center, point, angle);

                    let rotateAngle = 360 - angle;

                    let translateY = 8;
                    if (rotateAngle > 350) {
                        translateY += (10 - (360 - rotateAngle)) / 2;
                    } else if (rotateAngle < 10) {
                        translateY += (10 - rotateAngle) / 2;
                    }

                    return 'translate(' + (rotatedPoint.x * .80) + ', ' + translateY + ') rotate(' + (360 - angle) + ')';
                });
            }
        });
    }

    function appendGraph(container) {


        svg = container.append('svg')
            .attr('width', '100%')
            .attr('height', '100%')
            .attr('class', 'stardogd3-graph')
            .call(d3.zoom().on('zoom', function () {
                let scale = d3.event.transform.k,
                    translate = [d3.event.transform.x, d3.event.transform.y];

                if (svgTranslate) {
                    translate[0] += svgTranslate[0];
                    translate[1] += svgTranslate[1];
                }

                if (svgScale) {
                    scale *= svgScale;
                }

                svg.attr('transform', 'translate(' + translate[0] + ', ' + translate[1] + ') scale(' + scale + ')');
            }))
            .on('dblclick.zoom', null)
            .append('g');

        if (options.relationshipIcon && endCaps.find(e => e.name === options.relationshipIcon)) {

            let defs = svg.append('defs');

            defs.selectAll('marker')
                .data(endCaps)
                .enter()
                .append('marker')
                .attr('id', function (d) {
                    return 'marker_' + d.name
                })
                .attr('markerHeight', options.relationshipIconSize)
                .attr('markerWidth', options.relationshipIconSize)
                .attr('markerUnits', 'strokeWidth')
                .attr('orient', 'auto')
                .attr('refX', 0)
                .attr('refY', 0)
                .attr('viewBox', function (d) {
                    return d.viewbox
                })
                .append('path')
                .attr('d', function (d) {
                    return d.path
                })
                .attr('fill', options.relationshipColor);
        }

        svgRelationships = svg.append('g')
            .attr('class', 'relationships');

        svgNodes = svg.append('g')
            .attr('class', 'nodes');

    }

    function appendNode() {
        return node.enter()
            .append('g')
            .attr('class', function (d) {
                return 'node';
            })
            .on('click', function (d) {

            })
            .on('dblclick', function (d) {
                stickNode(d);

            })
            .on('mouseenter', function (d) {
                d3.select(this).select('.ring').attr('class', 'ringOn');
            })
            .on('mouseleave', function (d) {
                d3.select(this).select('.ringOn').attr('class', 'ring');
            })
            .call(d3.drag()
                .on('start', dragStarted)
                .on('drag', dragged)
                .on('end', dragEnded));
    }

    function appendNodeToGraph() {
        let n = appendNode();

        appendRingToNode(n);
        appendOutlineToNode(n);
        appendTextToNode(n);

        return n;
    }

    function appendOutlineToNode(node) {
        return node.append('circle')
            .attr('class', 'outline')
            .attr('r', options.nodeRadius)
            .style('fill', function (d) {
                return options.nodeOutlineFillColor ? options.nodeOutlineFillColor : class2color(d.label);
            })
            .style('stroke', function (d) {
                return options.nodeOutlineFillColor ? class2darkenColor(options.nodeOutlineFillColor) : class2darkenColor(d.label);
            })
            .style('opacity', '1')
            .append('title').text(function (d) {
                return toString(d);
            });
    }

    function appendRingToNode(node) {
        return node.append('circle')
            .attr('class', 'ring')
            .attr('r', options.nodeRadius * 1.2)
            .append('title').text(function (d) {
                return toString(d);
            });
    }

    function appendTextToNode(node) {

        return node.append('text')
            .attr('class', 'text')
            .attr('fill', '#000000')
            .attr('font-size', '10px')
            .attr('pointer-events', 'none')
            .attr('text-anchor', 'middle')
            .attr('y', '4px')
            .text((d) => {
                return d.label;
            });
    }

    function appendRelationship() {
        return relationship.enter()
            .append('g')
            .attr('class', 'relationship')
            .on('dblclick', function (d) {

            })
            .on('mouseenter', function (d) {
                let opposingRelationship;
                if (options.showRelationshipCurvedEdges) {
                    opposingRelationship = relationships.find(r =>
                        r.source.id === d.target.id &&
                        r.target.id === d.source.id);
                }

                if (opposingRelationship) {
                    d3.select(this).select('.overlay').attr('class', 'overlayCurveOn');
                } else {
                    d3.select(this).select('.overlay').attr('class', 'overlayOn');
                }
            })
            .on('mouseleave', function (d) {
                let opposingRelationship;
                if (options.showRelationshipCurvedEdges) {
                    opposingRelationship = relationships.find(r =>
                        r.source.id === d.target.id &&
                        r.target.id === d.source.id);
                }

                if (opposingRelationship) {
                    d3.select(this).select('.overlayCurveOn').attr('class', 'overlay');
                } else {
                    d3.select(this).select('.overlayOn').attr('class', 'overlay');
                }
            })
    }

    function appendOutlineToRelationship(r) {
        return r.append('path')
            .attr('class', 'outline')
            .attr('fill', 'none')
            .attr("stroke", options.relationshipColor)
            .attr("stroke-width","1px")
            .attr('stroke-linecap', 'round')
            .attr("opacity", options.relationshipOpacity)
            .attr("marker-end", "url(#marker_" + options.relationshipIcon + ")");
    }

    function appendOverlayToRelationship(r) {
        return r.append('path')
            .attr('class', 'overlay');
    }

    function appendTextToRelationship(r) {
        return r.append('text')
            .attr('class', 'text')
            .attr('fill', '#000000')
            .attr('font-size', '8px')
            .attr('pointer-events', 'none')
            .attr('text-anchor', 'middle')
            .text(function (d) {
                return options.showRelationshipTitles ? d.linkType : '';
            });
    }

    function appendRelationshipToGraph() {
        var relationship = appendRelationship(),
            text = appendTextToRelationship(relationship),
            outline = appendOutlineToRelationship(relationship),
            overlay = appendOverlayToRelationship(relationship);

        return {
            outline: outline,
            overlay: overlay,
            relationship: relationship,
            text: text
        };
    }

    function dragEnded(d) {
        if (!d3.event.active) {
            simulation.alphaTarget(0);
        }

        if (typeof options.onNodeDragEnd === 'function') {
            options.onNodeDragEnd(d);
        }
    }

    function dragged(d) {
        stickNode(d);
    }

    function dragStarted(d) {
        if (!d3.event.active) {
            simulation.alphaTarget(0.3).restart();
        }

        d.fx = d.x;
        d.fy = d.y;
    }


    function merge(target, source) {
        Object.keys(source).forEach(function (property) {
            target[property] = source[property];
        });
    }

    function rotate(cx, cy, x, y, angle) {
        let radians = (Math.PI / 180) * angle,
            cos = Math.cos(radians),
            sin = Math.sin(radians),
            nx = (cos * (x - cx)) + (sin * (y - cy)) + cx,
            ny = (cos * (y - cy)) - (sin * (x - cx)) + cy;

        return {x: nx, y: ny};
    }

    function rotatePoint(c, p, angle) {
        return rotate(c.x, c.y, p.x, p.y, angle);
    }

    function rotation(source, target) {
        return Math.atan2(target.y - source.y, target.x - source.x) * 180 / Math.PI;
    }

    function size() {
        return {
            nodes: nodes.length,
            relationships: relationships.length
        };
    }

    function toString(d) {
        let s = d.label ? d.label : d.type ? d.type : 'NA';

        s += ' (<id>: ' + d.id;

        // Object.keys(d.properties).forEach(function (property) {
        //     s += ', ' + property + ': ' + JSON.stringify(d.properties[property]);
        // });

        s += ')';

        return s;
    }

    function unitaryNormalVector(source, target, newLength) {
        let center = {x: 0, y: 0},
            vector = unitaryVector(source, target, newLength);

        return rotatePoint(center, vector, 90);
    }

    function unitaryVector(source, target, newLength) {
        let length = Math.sqrt(Math.pow(target.x - source.x, 2) + Math.pow(target.y - source.y, 2)) / Math.sqrt(newLength || 1);

        return {
            x: (target.x - source.x) / length,
            y: (target.y - source.y) / length,
        };
    }

    function updateWithD3Data(d3Data) {
        updateNodesAndRelationships(d3Data.nodes, d3Data.relationships);
    }

    function updateWithStardogData(stardogData) {
        updateWithD3Data(stardogData);
    }

    function updateNodes(n) {


        nodes = n;


        node = svgNodes.selectAll('.node')
            .data(nodes, (d) => {
                return d.id;
            });

        let nodeEnter = appendNodeToGraph();
        node = nodeEnter.merge(node);

        // // Removes the nodes
        // svgNodes.selectAll('.node')
        //     .data(nodes, (d) => {
        //         return d.id;
        //     }).exit().remove();
    }

    function updateNodesAndRelationships(n, r) {

        updateNodes(n);
        updateRelationships(r);

        simulation.nodes(nodes);
        simulation.force('link').links(relationships);
        simulation.alpha(0.3).restart();
    }

    function updateRelationships(r) {

        relationships = r;
        relationship = svgRelationships.selectAll('.relationship')
            .data(r, (d) => {
                return d.id;
            });

        let relationshipEnter = appendRelationshipToGraph();

        relationship = relationshipEnter.relationship.merge(relationship);

        relationshipText = svg.selectAll('.relationship .text');
        relationshipText = relationshipEnter.text.merge(relationshipText);

        relationshipOutline = svg.selectAll('.relationship .outline');
        relationshipOutline = relationshipEnter.outline.merge(relationshipOutline);

        relationshipOverlay = svg.selectAll('.relationship .overlay');
        relationshipOverlay = relationshipEnter.overlay.merge(relationshipOverlay);


        svgRelationships.selectAll('.relationship')
            .data(relationships, function (d) {
                return d.id;
            }).exit().remove();
    }

    function version() {
        return VERSION;
    }

    function zoomFit() {
        let bounds = svg.node().getBBox(),
            parent = svg.node().parentElement.parentElement,
            fullWidth = parent.clientWidth,
            fullHeight = parent.clientHeight,
            width = bounds.width,
            height = bounds.height,
            midX = bounds.x + width / 2,
            midY = bounds.y + height / 2;

        if (width === 0 || height === 0) {
            return; // nothing to fit
        }

        svgScale = 0.85 / Math.max(width / fullWidth, height / fullHeight);
        svgTranslate = [fullWidth / 2 - svgScale * midX, fullHeight / 2 - svgScale * midY];

        svg.attr('transform', 'translate(' + svgTranslate[0] + ', ' + svgTranslate[1] + ') scale(' + svgScale + ')');
    }

    init(_selector, _options);

    return {

        size: size,
        updateWithD3Data: updateWithD3Data,
        updateWithStardogData: updateWithStardogData,
        version: version
    };


    function colors() {
        // d3.schemeCategory10,
        // d3.schemeCategory20,
        return [
            '#68bdf6', // light blue
            '#6dce9e', // green #1
            '#faafc2', // light pink
            '#f2baf6', // purple
            '#ff928c', // light red
            '#fcea7e', // light yellow
            '#ffc766', // light orange
            '#405f9e', // navy blue
            '#a5abb6', // dark gray
            '#78cecb', // green #2,
            '#b88cbb', // dark purple
            '#ced2d9', // light gray
            '#e84646', // dark red
            '#fa5f86', // dark pink
            '#ffab1a', // dark orange
            '#fcda19', // dark yellow
            '#797b80', // black
            '#c9d96f', // pistacchio
            '#47991f', // green #3
            '#70edee', // turquoise
            '#ff75ea'  // pink
        ];
    }

    function class2darkenColor(cls) {
        return d3.rgb(class2color(cls)).darker(1);
    }

    function class2color(cls) {
        let color = options.classes2colors[cls];

        if (!color) {
//            color = options.colors[Math.min(numClasses, options.colors.length - 1)];
            color = options.colors[numClasses % options.colors.length];
            options.classes2colors[cls] = color;
            numClasses++;
        }

        return color;
    }
}

/*
MIT License

Copyright (c) 2016 Eduardo Eisman

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

