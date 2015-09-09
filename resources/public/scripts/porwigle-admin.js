// Really really simple publish-subscribe module
// TODO: separate and test
var pubsub = (function() {
  var events = {};

  return {
    subscribe: function(id, listener) {
      if(events[id] == null) {
        events[id] = [listener];
      } else {
        events[id].push(listener);
      }
      console.log("Added listener to " + id + " has " + events[id].length + " listeners");
    },
    publish: function(id, data) {
      if(events[id] == null) {
        console.log("No listeners for " + id);
      }
      for(i = 0; i < events[id].length; i++) {
        events[id][i](data);
      }
    }
  };
})();

var PorwigleNode = React.createClass({
  handleClick: function() {
    pubsub.publish("OPEN_PAGE", this.props.node);
  },
  render: function() {
    var parentLevel = this.props.level;
    children = this.props.node.children.map(function (node) {
      return (
        <PorwigleNode key={node.id} level={parentLevel+1} node={node}></PorwigleNode>
      );
    });

    var classes = "porwigle-node " + (this.props.level == 0 ? "root" : "");

    return (
      <div className={classes}>
        <span className="porwigle-node-title" onClick={this.handleClick}>{this.props.node.title}</span>
        <div>{children}</div>
      </div>
    );
  }
});

var PorwigleStructure = React.createClass({
  render: function() {
    return (
      <div className="panel panel-default porwigle-pagestructure">
        <div className="panel-heading">
          <h3 className="panel-title">Page structure</h3>
        </div>
        <div className="panel-body">
          <PorwigleNode level={0} node={this.props.root}></PorwigleNode>
        </div>
      </div>
    );
  }
});

var PorwigleEditor = React.createClass({
    render: function() {
      var content = this.props.openedPage == null ? "" : this.props.openedPage.content;
      return (
        <div>
        <textarea className="form-control" rows="50" cols="50" value={content}></textarea>
        </div>
      );
    }
});

var Porwigle = React.createClass({
  getInitialState: function() {
    return {data: {children: []}};
  },
  openPage: function(page) {
    this.setState(React.addons.update(this.state, {openedPage: {$set: page}}));
  },
  componentDidMount: function() {
    pubsub.subscribe("OPEN_PAGE", this.openPage);

    $.ajax({
      url: 'http://localhost:8081/_api/structure',
      dataType: 'json',
      cache: false,
      success: function(data) {
        this.setState({data: data, openedPage: null});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    return (
    <div className="row porwigle-workspace">
      <div className="col-md-3"><PorwigleStructure root={this.state.data}/></div>
      <div className="col-md-7">
        <PorwigleEditor openedPage={this.state.openedPage}/>
      </div>
    </div>
    );
  }
});

React.render(
  <div>
    <div className="row">
      <div className="col-md-12"><h1>Porwigle</h1></div>
    </div>
    <Porwigle/>
  </div>,
  document.getElementById('container')
);
