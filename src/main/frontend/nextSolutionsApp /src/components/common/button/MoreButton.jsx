import AddIcon from '../../common/icon/AddIcon';

const MoreButton = ({ href }) => {
  return (
    <a href={href} className="flex justify-end hover:opacity-80">
      <AddIcon size="20px" />
      <span className="text-sm text-gray-500">더보기</span>
    </a>
  );
};

export default MoreButton;
