var PorwigleNode = React.createClass({
  render: function() {
    console.log(this.props);
    var parentLevel = this.props.level;
    children = this.props.children.map(function (node) {
      return (
        <PorwigleNode level={parentLevel+1} title={node.title} children={node.children}></PorwigleNode>
      );
    });

    var style = {
      marginLeft: this.props.level + "em"
    };

    return (
      <div style={style}>{this.props.title}
      <div>{children}</div>
      </div>
    );
  }
});

var PorwigleStructure = React.createClass({
  getInitialState: function() {
    return {data: {children: []}};
  },
  componentDidMount: function() {
    $.ajax({
      url: 'http://localhost:8081/_api/structure',
      dataType: 'json',
      cache: false,
      success: function(data) {
        console.log(data);
        this.setState({data: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    return (
      <div>
        <h1>Page Structure</h1>
        <PorwigleNode level={0} title={this.state.data.title} children={this.state.data.children}></PorwigleNode>
      </div>
    );
  }
});

React.render(
  <PorwigleStructure/>,
  document.getElementById('container')
);
