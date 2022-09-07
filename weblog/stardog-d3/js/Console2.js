import React from 'js/react.min';
import { PropTypes } from 'prop-types';

class Console2 extends React.Component {
    constructor(props) {
        super(props);

        this.state = {tabIndex: 0};
    }

    updateTabIndex(value) {
        this.setState({tabIndex: value})
    };

    render() {
        return (
            <div>
                <div className="console">
                    <div className="tabContent">
                        <div onClick={this.updateTabIndex.bind(this, 0)} className={this.state.tabIndex === 0 ? 'tab tabOn' : 'tab'}>Add Person</div>
                        <div onClick={this.updateTabIndex.bind(this, 1)} className={this.state.tabIndex === 1 ? 'tab tabOn' : 'tab'}>Add Relationship</div>
                        <div onClick={this.updateTabIndex.bind(this, 2)} className={this.state.tabIndex === 2 ? 'tab tabOn' : 'tab'}>Query Editor</div>
                    </div>
                    <div className="consoleBody">
                        Body
                    </div>
                </div>
            </div>
        );
    }
}

Console2.propTypes = { };
Console2.defaultValue = { };