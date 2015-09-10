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
    var marked = this.props.marked;
    children = this.props.node.children.map(function (node) {
      return (
        <PorwigleNode key={node.id} level={parentLevel+1} node={node} marked={marked}></PorwigleNode>
      );
    });

    var classes = "porwigle-node" +
        (this.props.level == 0 ? " root" : "");
    var titleClasses = "porwigle-node-title" +
        (this.props.marked == this.props.node.id ? " marked" : "");

    return (
      <div className={classes}>
        <span className={titleClasses} onClick={this.handleClick}>{this.props.node.title}</span>
        <div>{children}</div>
      </div>
    );
  }
});

var PorwigleStructure = React.createClass({
  render: function() {
    return (
      <PorwigleNode level={0} node={this.props.root} marked={this.props.markednode}></PorwigleNode>
    );
  }
});

var PorwigleEditor = React.createClass({
    render: function() {
      if(!this.props.page) {
        return (<div className="alert alert-info">
                  <strong>G'day!!</strong> Open page from left to edit your site like there is no tomorrow.
                </div>);
      }
      return (
        <div>
          <ul className="nav nav-tabs" role="tablist">
            <li role="presentation" className="active"><a href="#content" aria-controls="content" role="tab" data-toggle="tab">Content</a></li>
            <li role="presentation"><a href="#fields" aria-controls="fields" role="tab" data-toggle="tab">Fields</a></li>
          </ul>
          <div className="tab-content">
            <div role="tabpanel" className="tab-pane active" id="content">
              <p>This is content in this page.</p>
              <form role="form">
                <div className="form-group">
                    <textarea className="form-control" rows="20" cols="50" value={this.props.page.content}></textarea>
                </div>
              </form>
            </div>
            <div role="tabpanel" className="tab-pane" id="fields">
              <p>These are fields registered to this page.</p>
              <form role="form">
                <div className="form-group">
                  <div className="input-group">
                    <span className="input-group-addon" id="uri">Title</span>
                    <input type="text" className="form-control" value={this.props.page.title} aria-describedby="uri"/>
                  </div>
                </div>
                <div className="form-group">
                  <div className="input-group">
                    <span className="input-group-addon" id="uri">URI</span>
                    <input type="text" className="form-control" value={this.props.page.urn} aria-describedby="uri"/>
                  </div>
                </div>
              </form>
            </div>
          </div>
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
    var markedNodeId = this.state.openedPage != null ? this.state.openedPage.id : null;
    return (
      <div className="row porwigle-workspace">
        <div className="col-md-3">
          <div className="panel panel-default porwigle-pagestructure">
            <div className="panel-heading">
              <h3 className="panel-title">Page structure</h3>
            </div>
            <div className="panel-body">
              <PorwigleStructure root={this.state.data}
                markednode={markedNodeId}/>
            </div>
          </div>
        </div>
        <div className="col-md-7">
          <div className="panel panel-default porwigle-editor">
            <div className="panel-heading">
              <h3 className="panel-title">Editor</h3>
            </div>
            <div className="panel-body">
              <PorwigleEditor page={this.state.openedPage}/>
            </div>
          </div>
        </div>
        <div className="col-md-2">
          <div className="panel panel-default porwigle-infozone">
            <div className="panel-heading">
              <h3 className="panel-title">Infozone</h3>
            </div>
            <div className="panel-body">
              <p>Here you find hints how to use this fine fine fine content management system.</p>
            </div>
          </div>
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
